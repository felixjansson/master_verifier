package com.master_thesis.verifier;

import ch.qos.logback.classic.Logger;
import com.master_thesis.verifier.utils.PublicParameters;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Stream;

@Component
public class HomomorphicHash {

    private final static Logger log = (Logger) LoggerFactory.getLogger(HomomorphicHash.class);
    protected PublicParameters publicParameters;


    @Autowired
    public HomomorphicHash(PublicParameters publicParameters) {
        this.publicParameters = publicParameters;
    }


    public BigInteger finalEval(Stream<BigInteger> partialResultInfo) {
        return partialResultInfo
                .reduce(BigInteger.ZERO, BigInteger::add);
    }

    public BigInteger finalProof(Stream<BigInteger> partialProofs, int substationID) {
        return partialProofs
                .reduce(BigInteger.ONE, BigInteger::multiply)
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

}