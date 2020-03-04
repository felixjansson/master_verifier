package com.master_thesis.verifier;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class HomomorphicHash {

    protected PublicParameters publicParameters;
    private final static Logger log = (Logger) LoggerFactory.getLogger(HomomorphicHash.class);


    @Autowired
    public HomomorphicHash(PublicParameters publicParameters) {
        this.publicParameters = publicParameters;
    }


    public BigInteger finalEval(Map<Integer, BigInteger> partialResultInfo, int transformatorID) {
        return partialResultInfo.values()
                .stream()
                .reduce(BigInteger.ZERO, BigInteger::add)
                .mod(publicParameters.getFieldBase(transformatorID));
    }

    public BigInteger finalProof(List<BigInteger> partialProofsInfo, int transformatorID) {
        return paperFinalProof(partialProofsInfo, transformatorID);
//        Set<Integer> serverIDs = Set.copyOf(partialProofsInfo.keySet());
//        BigInteger fieldBase = publicParameters.getFieldBase(transformatorID);
//        return partialProofsInfo.keySet().stream().map(serverID -> {
//            int beta = beta(serverID, serverIDs);
//            BigInteger partialServerProof = partialProofsInfo.get(serverID);
//            BigInteger res = partialServerProof.modPow(BigInteger.valueOf(beta), fieldBase);
//            log.info("{} : {}, beta: {}, partial: {}", serverID, res, beta, partialServerProof);
//            return res;
//        }).reduce(BigInteger.ONE, (accu, x) -> accu.multiply(x).mod(fieldBase));
    }

    public BigInteger paperFinalProof(List<BigInteger> partialProofs, int transformatorID) {
        return partialProofs.stream()
                .reduce(BigInteger.ONE, BigInteger::multiply).mod(publicParameters.getFieldBase(transformatorID));
    }

    public boolean verify(int transformatorID, BigInteger result, BigInteger serverProof, List<BigInteger> clientProofs) {
        BigInteger fieldBase = publicParameters.getFieldBase(transformatorID);
        BigInteger clientProof = clientProofs.stream().reduce(BigInteger.ONE, BigInteger::multiply).mod(fieldBase);
        BigInteger resultProof = hash(result, fieldBase, publicParameters.getGenerator(transformatorID));
        boolean clientEqResult = clientProof.equals(resultProof);
        boolean clientEqServer = clientProof.equals(serverProof);
        if (!(clientEqResult && clientEqServer))
            log.info("clientProof: {}, resultProof: {}, serverProof:{}", clientProof, resultProof, serverProof);
        return clientProof.equals(resultProof) && clientProof.equals(serverProof);
    }

    public BigInteger hash(BigInteger input, BigInteger fieldBase, BigInteger generator) {
        return generator.modPow(input, fieldBase);
    }

    public int beta(int serverID, Set<Integer> serverIDs) {
//        Page 21 Lecture 9 in Krypto
        return (int) Math.round(serverIDs.stream().mapToDouble(Integer::doubleValue).reduce(1f, (prev, j) -> {
            if (j == serverID) {
                return prev;
            } else {
                return prev * (j / (j - serverID));
            }
        }));
    }
}