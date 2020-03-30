package com.master_thesis.verifier;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RestController
public class VerifierController {

    private static final Logger log = (Logger) LoggerFactory.getLogger(VerifierController.class);
    private VerifierBuffer buffer;
    private ClientBuffer clientBuffer;
    private Lock bufferLock;
    private RSAThreshold rsaThresholdVerifier;
    private HomomorphicHash homomorphicHashVerifier;
    private PublicParameters publicParameters;

    @Autowired
    public VerifierController(VerifierBuffer buffer, ClientBuffer clientBuffer, RSAThreshold rsaThresholdVerifier, HomomorphicHash homomorphicHashVerifier, PublicParameters publicParameters) {
        this.buffer = buffer;
        this.clientBuffer = clientBuffer;
        this.rsaThresholdVerifier = rsaThresholdVerifier;
        this.homomorphicHashVerifier = homomorphicHashVerifier;
        this.publicParameters = publicParameters;
        this.bufferLock = new ReentrantLock();
    }

    @PostMapping(value = "/api/partials")
    public void receiveShare(@RequestBody PartialInfo partialInfo) throws InterruptedException {
        boolean isUnlocked = bufferLock.tryLock(1, TimeUnit.SECONDS);
        if (isUnlocked) {
            try {
                log.info("Got {}", partialInfo);
                buffer.put(partialInfo);
                if (buffer.canCompute(partialInfo.getSubstationID(), partialInfo.getFid()))
                    new Thread(() -> performComputations(partialInfo.getSubstationID(), partialInfo.getFid())).start();
            } finally {
                bufferLock.unlock();
            }
        }
    }

    @PostMapping(value = "/client/proofComponent")
    void receiveProofComponent(@RequestBody PartialClientInfo partialClientInfo){
       clientBuffer.put(partialClientInfo);
    }

    private void performComputations(int substationID, int fid) {

        VerifierBuffer.Fid fidData = buffer.getFid(substationID, fid);
        BigInteger lastClientProof = publicParameters.getLastClientProof(substationID, fid);

        // Homomorphic Hash verification
        List<BigInteger> clientProofs = clientBuffer.getClientProofs(substationID,fid);
        clientProofs.add(lastClientProof);
        clientBuffer.removeFid(substationID, fid);
        BigInteger hashResult = homomorphicHashVerifier.finalEval(fidData.getPartialResultsInfo(substationID), substationID);
        BigInteger hashServerProof = homomorphicHashVerifier.finalProof(fidData.getHomomorphicPartialProofs(), substationID);
        boolean hashValidResult = homomorphicHashVerifier.verify(substationID, hashResult, hashServerProof, clientProofs);
        log.info("Hash: Writing to DB: result:{} server proof:{} valid:{}", hashResult, hashServerProof, hashValidResult);

        // RSA verification
        if (fidData.hasRSAComponents()){
            List<ClientInfo> rsaProofComponents = fidData.getRSAProofComponents();
            BigInteger rsaResult = rsaThresholdVerifier.finalEval(fidData.getPartialResultsInfo(substationID), substationID);
            BigInteger rsaServerProof = rsaThresholdVerifier.finalProof(rsaProofComponents, substationID, lastClientProof);
            boolean rsaValidResult = rsaThresholdVerifier.verify(substationID, rsaResult, rsaServerProof, clientProofs);
            log.info("RSA: Writing to DB: result:{} server proof:{} valid:{}", rsaResult, rsaServerProof, rsaValidResult);
        }

//        DatabaseConnection.put(rsaResult, rsaServerProof, rsaValidResult);
    }

}
