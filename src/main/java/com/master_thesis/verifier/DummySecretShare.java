package com.master_thesis.verifier;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DummySecretShare implements VerifierSecretShare{
    @Override
    public int finalEval(List<Integer> partialEvaluations) {
        return partialEvaluations.stream().reduce(0, Integer::sum);
    }

    @Override
    public int finalProof(List<Integer> partialProofs) {
        return 0;
    }

    @Override
    public boolean verify(Object pp, Object sk, int proof, int result) {
        return true;
    }
}
