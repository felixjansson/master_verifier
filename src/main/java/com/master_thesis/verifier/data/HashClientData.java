package com.master_thesis.verifier.data;

import java.math.BigInteger;

public class HashClientData extends ComputationData {
    private BigInteger proofComponent;

    protected HashClientData() {
        super(Construction.HASH);
    }

    public BigInteger getProofComponent() {
        return proofComponent;
    }

    public void setProofComponent(BigInteger proofComponent) {
        this.proofComponent = proofComponent;
    }

    @Override
    public String toString() {
        return "HashClientData{" +
                "clientProofComponent=" + proofComponent +
                "} " + super.toString();
    }
}
