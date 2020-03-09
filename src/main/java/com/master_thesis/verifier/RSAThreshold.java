package com.master_thesis.verifier;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Component
public class RSAThreshold extends HomomorphicHash {

    private final static Logger log = (Logger) LoggerFactory.getLogger(RSAThreshold.class);

    @Autowired
    public RSAThreshold(PublicParameters publicParameters) {
        super(publicParameters);
    }

    public BigInteger rsaFinalProof(List<ClientInfo> rsaProofComponents, int transformatorID) {
        if (rsaProofComponents.isEmpty())
            return null;
        return rsaProofComponents.stream()
                .map(rsaProofComponent -> {
                    BigInteger clientProof = clientFinalProof(
                            rsaProofComponent.getPublicKey(),
                            rsaProofComponent.getClientProof(),
                            rsaProofComponent.getRsaProofComponent(),
                            rsaProofComponent.getRsaN(),
                            rsaProofComponent.getRsaDeterminant());
                    return clientProof.modPow(rsaProofComponent.getPublicKey(), publicParameters.getFieldBase(transformatorID));
                })
                .reduce(BigInteger.ONE, BigInteger::multiply)
                .mod(publicParameters.getFieldBase(transformatorID));
    }

    private BigInteger clientFinalProof(BigInteger pk, BigInteger clientProof, BigInteger[] serverProofs, BigInteger rsaN, double determinant) {

        BigInteger partial = Arrays.stream(serverProofs).reduce(BigInteger.ONE, BigInteger::multiply).mod(rsaN);
        Random r = new Random();
        int alpha = r.nextInt();
        Optional<Integer> betaOpt = Optional.empty();
        while (betaOpt.isEmpty()) {
            log.debug("{} is not a valid alpha", alpha);
            betaOpt = computeBeta(pk, BigInteger.valueOf(Math.round(determinant)), alpha); // TODO: 2020-03-09 This seems work
            alpha = r.nextInt();
        }
        int beta = betaOpt.get();

        return partial
                .modPow(BigInteger.valueOf(alpha), rsaN)
                .multiply(clientProof.modPow(BigInteger.valueOf(beta), rsaN))
                .mod(rsaN);
    }


    private Optional<Integer> computeBeta(BigInteger pk, BigInteger determinant, int alpha) {
        BigInteger numerator = BigInteger.ONE.subtract(determinant.multiply(BigInteger.valueOf(alpha * 2)));
        if (numerator.remainder(pk).equals(BigInteger.ZERO)) {
            return Optional.of(numerator.divide(pk).intValue());
        }
        return Optional.empty();
    }

}
