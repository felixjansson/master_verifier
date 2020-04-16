package com.master_thesis.verifier.data;

import java.math.BigInteger;

public class NonceClientData extends ComputationData {
    private BigInteger proofComponent;

    protected NonceClientData() {
        super(Construction.NONCE);
    }

    public BigInteger getProofComponent() {
        return proofComponent;
    }

    public void setProofComponent(BigInteger proofComponent) {
        this.proofComponent = proofComponent;
    }

    @Override
    public String toString() {
        return "NonceClientData{" +
                "proofComponent=" + proofComponent +
                "} " + super.toString();
    }
}
