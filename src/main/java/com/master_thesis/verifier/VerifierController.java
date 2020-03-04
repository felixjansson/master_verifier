package com.master_thesis.verifier;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.List;

@RestController
public class VerifierController {

    private VerifierBuffer buffer;
    private RSAThreshold verifier;
    private static final Logger log = (Logger) LoggerFactory.getLogger(VerifierController.class);

    @Autowired
    public VerifierController(VerifierBuffer buffer, RSAThreshold verifier) {
        this.buffer = buffer;
        this.verifier = verifier;
    }

    @PostMapping(value = "/api/partials")
    public void receiveShare(@RequestBody PartialInfo partialInfo) {
        log.info("Got {}", partialInfo);
        buffer.put(partialInfo);
        if (buffer.canCompute())
            new Thread(() -> performComputations(partialInfo.getTransformatorID())).start();
    }

    private void performComputations(int transformatorID) {
        BigInteger result = verifier.finalEval(buffer.getPartialResultsInfo(transformatorID), transformatorID);
        List<BigInteger> clientProofs = buffer.getClientProofs(transformatorID);

        BigInteger rsaServerProof = verifier.newFinalProof(buffer.getRSAProofComponents(transformatorID), transformatorID);
        BigInteger hashServerProof = verifier.finalProof(buffer.getClientProofs(transformatorID), transformatorID);

        buffer.pop();
        boolean rsaValidResult = verifier.verify(transformatorID, result, rsaServerProof, clientProofs);
        boolean hashValidResult = verifier.verify(transformatorID, result, hashServerProof, clientProofs);
        log.info("RSA: Writing to DB: result:{} server proof:{} valid:{}", result, rsaServerProof, rsaValidResult);
        log.info("Hash: Writing to DB: result:{} server proof:{} valid:{}", result, hashServerProof, hashValidResult);
        DatabaseConnection.put(result, rsaServerProof, rsaValidResult);
    }

}
