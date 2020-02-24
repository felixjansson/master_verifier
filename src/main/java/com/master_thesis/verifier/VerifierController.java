package com.master_thesis.verifier;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
            int result = verifier.finalEval(buffer.getPartialResults());
            int proof = verifier.finalProof(buffer.getPartialProofs());
            buffer.pop();
            boolean validResult = verifier.verify(null, null, proof, result);
            log.info("Writing to DB: result:{} proof:{} valid:{}", result, proof, validResult);
            DatabaseConnection.put(result, proof, validResult);
        }
    }


}
