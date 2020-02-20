package com.master_thesis.verifier;

import java.util.List;

public interface VerifierSecretShare {

    int finalEval(List<Integer> partialEvaluations);
    int finalProof(List<Integer> partialProofs);

    boolean verify(Object pp, Object sk, int proof, int result);
}
