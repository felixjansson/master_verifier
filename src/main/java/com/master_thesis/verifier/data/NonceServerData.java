package com.master_thesis.verifier.data;

import java.math.BigInteger;

public class NonceServerData extends ComputationData {
    private BigInteger partialResult;
    private BigInteger partialProof;
    private BigInteger partialNonce;


    public NonceServerData() {
        super(Construction.NONCE);
    }

    public BigInteger getPartialResult() {
        return partialResult;
    }

    public void setPartialResult(BigInteger partialResult) {
        this.partialResult = partialResult;
    }

    public BigInteger getPartialProof() {
        return partialProof;
    }

    public void setPartialProof(BigInteger partialProof) {
        this.partialProof = partialProof;
    }

    public BigInteger getPartialNonce() {
        return partialNonce;
    }

    public void setPartialNonce(BigInteger partialNonce) {
        this.partialNonce = partialNonce;
    }

    @Override
    public String toString() {
        return "NonceServerData{" +
                "partialResult=" + partialResult +
                ", partialProof=" + partialProof +
                ", partialNonce=" + partialNonce +
                "} " + super.toString();
    }
}
