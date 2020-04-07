package com.master_thesis.verifier.data;

import java.math.BigInteger;

public class LinearServerData extends ComputationData {

    private BigInteger partialResult;

    public LinearServerData() {
        super(Construction.LINEAR);
    }

    public BigInteger getPartialResult() {
        return partialResult;
    }

    public void setPartialResult(BigInteger partialResult) {
        this.partialResult = partialResult;
    }


}
