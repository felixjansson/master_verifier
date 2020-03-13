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

    @Autowired
    public VerifierController(VerifierBuffer buffer, RSAThreshold verifier) {
        this.buffer = buffer;
        this.verifier = verifier;
        this.bufferLock = new ReentrantLock();
    }

    @PostMapping(value = "/api/partials")
    public void receiveShare(@RequestBody PartialInfo partialInfo) throws InterruptedException {
        boolean isUnlocked = bufferLock.tryLock(1, TimeUnit.SECONDS);
        if (isUnlocked) {
            try {
                log.info("Got {}", partialInfo);
                buffer.put(partialInfo);
                if (buffer.canCompute())
                    new Thread(() -> performComputations(partialInfo.getTransformatorID())).start();
            } finally {
                bufferLock.unlock();
            }
        }
    }

    private void performComputations(int transformatorID) {

        BigInteger result = verifier.finalEval(buffer.getPartialResultsInfo(transformatorID), transformatorID);
        List<BigInteger> clientProofs = buffer.getClientProofs(transformatorID);

        BigInteger rsaServerProof = verifier.rsaFinalProof(buffer.getRSAProofComponents(transformatorID), transformatorID, buffer);
        BigInteger hashServerProof = verifier.hashFinalProof(buffer.getClientProofs(transformatorID), transformatorID);

        buffer.pop();
        boolean rsaValidResult = verifier.verify(transformatorID, result, rsaServerProof, clientProofs);
        boolean hashValidResult = verifier.verify(transformatorID, result, hashServerProof, clientProofs);
        log.info("RSA: Writing to DB: result:{} server proof:{} valid:{}", result, rsaServerProof, rsaValidResult);
        log.info("Hash: Writing to DB: result:{} server proof:{} valid:{}", result, hashServerProof, hashValidResult);
        DatabaseConnection.put(result, rsaServerProof, rsaValidResult);
    }

}
