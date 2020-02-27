package com.master_thesis.verifier;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface VerifierSecretShare {

    BigInteger finalEval(Map<Integer, BigInteger> partialResultInfo, int transformatorID);
    BigInteger finalProof(Map<Integer, BigInteger> partialProofs, int transformatorID);

    boolean verify(int transformatorID, BigInteger result, BigInteger  serverProof, List<BigInteger> clientProofs);
}
