package com.master_thesis.verifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VerifierController {

    private VerifierBuffer buffer;
    private VerifierSecretShare verifier;

    @Autowired
    public VerifierController(VerifierBuffer buffer, VerifierSecretShare verifier) {
        this.buffer = buffer;
        this.verifier = verifier;
    }

    @PostMapping(value = "/api/partials")
    public void receiveShare(@RequestBody PartialInfo partialInfo) {
        buffer.put(partialInfo);
        if (buffer.canCompute()) {
            int result = verifier.finalEval(buffer.getPartialEvals());
            int proof = verifier.finalProof(buffer.getPartialProofs());
            boolean validResult = verifier.verify(null, null, proof, result);
            DatabaseConnection.put(result, proof, validResult);
        }
    }


}
