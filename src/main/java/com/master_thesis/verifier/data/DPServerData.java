package com.master_thesis.verifier.data;

import java.math.BigInteger;

public class DPServerData extends ComputationData {

    private BigInteger partialProof;
    private BigInteger partialResult;

    protected DPServerData() {
        super(Construction.DP);
    }

    public BigInteger getPartialProof() {
        return partialProof;
    }

    public void setPartialProof(BigInteger partialProof) {
        this.partialProof = partialProof;
    }

    public BigInteger getPartialResult() {
        return partialResult;
    }

    public void setPartialResult(BigInteger partialResult) {
        this.partialResult = partialResult;
    }

    @Override
    public String toString() {
        return "DPServerData{" +
                "partialProof=" + partialProof +
                ", partialResult=" + partialResult +
                "} " + super.toString();
    }
}
