package com.master_thesis.verifier;

import ch.qos.logback.classic.Logger;
import com.master_thesis.verifier.utils.PublicParameters;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Stream;

@Component
public class NonceDistribution {

    private final static Logger log = (Logger) LoggerFactory.getLogger(NonceDistribution.class);
    protected PublicParameters publicParameters;


    @Autowired
    public NonceDistribution(PublicParameters publicParameters) {
        this.publicParameters = publicParameters;
    }


    public BigInteger finalEval(Stream<BigInteger> partialResultInfo, int substationID) {
        return partialResultInfo
                .reduce(BigInteger.ZERO, BigInteger::add);
    }

    public BigInteger finalProof(Stream<BigInteger> partialProofs, int substationID) {
        return partialProofs
                .reduce(BigInteger.ONE, BigInteger::multiply)
                .mod(publicParameters.getFieldBase(substationID));
    }

    public BigInteger finalNonce(Stream<BigInteger> partialResultInfo, int substationID) {
        BigInteger fieldBase = publicParameters.getFieldBase(substationID);
        BigInteger nonceSum = partialResultInfo
                .reduce(BigInteger.ZERO, BigInteger::add);
        BigInteger totient = fieldBase.subtract(BigInteger.ONE);
        BigDecimal sum = new BigDecimal(nonceSum);
        BigDecimal tot = new BigDecimal(totient);
        BigInteger ceil = sum.divide(tot, RoundingMode.CEILING).toBigInteger();
        return totient.multiply(ceil).subtract(nonceSum);
    }

    public boolean verify(int substationID, BigInteger result, BigInteger serverProof, List<BigInteger> clientProofs, BigInteger finalNonce) {
        if (serverProof == null)
            return false;
        BigInteger fieldBase = publicParameters.getFieldBase(substationID);
        BigInteger hashFinalNonce = hash(finalNonce, fieldBase, publicParameters.getGenerator(substationID));
        BigInteger clientProof = clientProofs.stream().reduce(BigInteger.ONE, BigInteger::multiply).multiply(hashFinalNonce).mod(fieldBase);
        BigInteger resultProof = hash(result, fieldBase, publicParameters.getGenerator(substationID));
        boolean clientEqResult = clientProof.equals(resultProof);
        boolean serverEqResult = serverProof.equals(resultProof);
        if (!(clientEqResult && serverEqResult))
            log.error("clientProof: {}, resultProof: {}, serverProof:{}", clientProof, resultProof, serverProof);
        return clientEqResult && serverEqResult;
    }

    public BigInteger hash(BigInteger input, BigInteger fieldBase, BigInteger generator) {
        return generator.modPow(input, fieldBase);
    }

}