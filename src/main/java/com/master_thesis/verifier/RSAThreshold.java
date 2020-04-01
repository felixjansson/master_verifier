package com.master_thesis.verifier;

import ch.qos.logback.classic.Logger;
import com.master_thesis.verifier.data.RSAServerData;
import com.master_thesis.verifier.utils.PublicParameters;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

@Component
public class RSAThreshold {

    private final static Logger log = (Logger) LoggerFactory.getLogger(RSAThreshold.class);
    private PublicParameters publicParameters;

    @Autowired
    public RSAThreshold(PublicParameters publicParameters) {
        this.publicParameters = publicParameters;
    }

    public BigInteger finalEval(Stream<BigInteger> partialResultInfo, int substationID) {
        return partialResultInfo
                .reduce(BigInteger.ZERO, BigInteger::add)
                .mod(publicParameters.getFieldBase(substationID));
    }

    public BigInteger finalProof(Collection<RSAServerData.ProofData> rsaProofComponents, int substationID, BigInteger lastClientProof) {
        if (rsaProofComponents.isEmpty())
            return null;
        return rsaProofComponents.stream()
                .map(rsaProofComponent -> {
                    try {
                        BigInteger encryptedRSAProof = clientFinalProof(
                                rsaProofComponent.getPublicKey(),
                                rsaProofComponent.getClientProof(),
                                rsaProofComponent.getRsaProofComponent(),
                                rsaProofComponent.getRsaN(),
                                rsaProofComponent.getRsaDeterminant());
                        BigInteger res = encryptedRSAProof.modPow(rsaProofComponent.getPublicKey(), rsaProofComponent.getRsaN());
                        return res;
                    } catch (Exception e) {
                        log.info(e.getMessage());
                    }
                    return null;
                })
                .reduce(lastClientProof, BigInteger::multiply)
                .mod(publicParameters.getFieldBase(substationID));
    }


    public boolean verify(int substationID, BigInteger result, BigInteger serverProof, List<BigInteger> clientProofs) {
        if (serverProof == null)
            return false;
        BigInteger fieldBase = publicParameters.getFieldBase(substationID);
        BigInteger clientProof = clientProofs.stream().reduce(BigInteger.ONE, BigInteger::multiply).mod(fieldBase);
        BigInteger resultProof = hash(result, fieldBase, publicParameters.getGenerator(substationID));
        boolean clientEqResult = clientProof.equals(resultProof);
        boolean clientEqServer = clientProof.equals(serverProof);
        if (!(clientEqResult && clientEqServer))
            log.info("clientProof: {}, resultProof: {}, serverProof:{}", clientProof, resultProof, serverProof);
        return clientProof.equals(resultProof) && clientProof.equals(serverProof);
    }

    public BigInteger hash(BigInteger input, BigInteger fieldBase, BigInteger generator) {
        return generator.modPow(input, fieldBase);
    }

    private BigInteger clientFinalProof(BigInteger pk, BigInteger clientProof, BigInteger[] serverProofs, BigInteger rsaN, double determinant) {

        BigInteger partial = Arrays.stream(serverProofs).reduce(ONE, BigInteger::multiply).mod(rsaN);

        // Find alpha and beta
        BigInteger det = BigInteger.valueOf(Math.round(determinant));
        BigInteger detSign = det.divide(det.abs());
        BigInteger[] eeaResult = extendedEuclideanAlgorithm(det.multiply(BigInteger.TWO), pk);
        BigInteger alpha = eeaResult[0].multiply(detSign);
        BigInteger beta = eeaResult[1];

        // Compute the clients' rsa proof component
        BigInteger sigmaRoofAlpha = partial.modPow(alpha, rsaN);
        BigInteger tauBeta = clientProof.modPow(beta, rsaN);
        return sigmaRoofAlpha.multiply(tauBeta).mod(rsaN);
    }

    /**
     * Returns the Bézout coefficients and gcd.
     * All input will be mapped to positive numbers by absolute value function.
     * The return coefficient can be multiplied by the original sign of that parameter to restore correct sign.
     *
     * @return [coefficient of a, coefficient of b, gcd].
     */
    public static BigInteger[] extendedEuclideanAlgorithm(BigInteger a, BigInteger b) {
        //output "Bézout coefficients:", (s[1], t[1])
        //output "greatest common divisor:", r[1]
        //output "quotients by the gcd:", (t[0], s[0])
        BigInteger aPos = a.abs();
        BigInteger bPos = b.abs();
        BigInteger[] s = new BigInteger[]{ZERO, ONE};
        BigInteger[] t = new BigInteger[]{ONE, ZERO};
        BigInteger[] r = new BigInteger[]{bPos.max(aPos), bPos.min(aPos)};

        while (!r[0].equals(ZERO)) {
            BigInteger quotient = r[1].divide(r[0]);
            r = internalEEA(r, quotient);
            s = internalEEA(s, quotient);
            t = internalEEA(t, quotient);
        }

        if (bPos.max(aPos).equals(bPos)) {
            return new BigInteger[]{s[1], t[1], r[1]};
        } else {
            return new BigInteger[]{t[1], s[1], r[1]};
        }
    }

    private static BigInteger[] internalEEA(BigInteger[] values, BigInteger q) {
        return new BigInteger[]{values[1].subtract(q.multiply(values[0])), values[0]};
    }

}
