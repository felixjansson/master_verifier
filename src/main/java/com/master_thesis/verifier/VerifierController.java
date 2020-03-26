package com.master_thesis.verifier;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RestController
public class VerifierController {

    private static final Logger log = (Logger) LoggerFactory.getLogger(VerifierController.class);
    private VerifierBuffer buffer;
    private Lock bufferLock;
    private RSAThreshold verifier;
    private PublicParameters publicParameters;

    @Autowired
    public VerifierController(VerifierBuffer buffer, RSAThreshold verifier, PublicParameters publicParameters) {
        this.buffer = buffer;
        this.verifier = verifier;
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

    private void performComputations(int substationID, int fid) {

        VerifierBuffer.Fid fidData = buffer.getFid(substationID, fid);

        BigInteger result = verifier.finalEval(fidData.getPartialResultsInfo(substationID), substationID);
        List<BigInteger> clientProofs = fidData.getClientProofs();
        BigInteger lastClientProof = publicParameters.getLastClientProof(substationID, fid);
        clientProofs.add(lastClientProof);

        BigInteger rsaServerProof = verifier.rsaFinalProof(fidData.getRSAProofComponents(), substationID, lastClientProof);
        BigInteger hashServerProof = verifier.hashFinalProof(clientProofs, substationID);

        boolean rsaValidResult = verifier.verify(substationID, result, rsaServerProof, clientProofs);
        boolean hashValidResult = verifier.verify(substationID, result, hashServerProof, clientProofs);
        log.info("RSA: Writing to DB: result:{} server proof:{} valid:{}", result, rsaServerProof, rsaValidResult);
        log.info("Hash: Writing to DB: result:{} server proof:{} valid:{}", result, hashServerProof, hashValidResult);
        DatabaseConnection.put(result, rsaServerProof, rsaValidResult);
    }

}
