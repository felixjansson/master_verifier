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
    private VerifierSecretShare verifier;
    private static final Logger log = (Logger) LoggerFactory.getLogger(VerifierController.class);

    @Autowired
    public VerifierController(VerifierBuffer buffer, VerifierSecretShare verifier) {
        this.buffer = buffer;
        this.verifier = verifier;
    }

    @PostMapping(value = "/api/partials")
    public void receiveShare(@RequestBody PartialInfo partialInfo) {
        log.info("Got {}", partialInfo);
        buffer.put(partialInfo);
        if (buffer.canCompute()) {
            int transformatorID = partialInfo.getTransformatorID();
            BigInteger result = verifier.finalEval(buffer.getPartialResultsInfo(transformatorID), transformatorID);
            BigInteger serverProof = verifier.finalProof(buffer.getServerPartialProofs(transformatorID), transformatorID);
            List<BigInteger> clientProofs = buffer.getClientProofs(transformatorID);
            buffer.pop();
            boolean validResult = verifier.verify(partialInfo.getTransformatorID(), result, serverProof, clientProofs);
            log.info("Writing to DB: result:{} proof:{} valid:{}", result, serverProof, validResult);
            DatabaseConnection.put(result, serverProof, validResult);
        }
    }


}
