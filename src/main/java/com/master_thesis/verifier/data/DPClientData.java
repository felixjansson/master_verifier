package com.master_thesis.verifier.data;

import java.math.BigInteger;

public class DPClientData extends ComputationData {
    private BigInteger proofComponent;

    protected DPClientData() {
        super(Construction.DP);
    }

    public BigInteger getProofComponent() {
        return proofComponent;
    }

    public void setProofComponent(BigInteger proofComponent) {
        this.proofComponent = proofComponent;
    }

    @Override
    public String toString() {
        return "DPClientData{" +
                "clientProofComponent=" + proofComponent +
                "} " + super.toString();
    }
}
