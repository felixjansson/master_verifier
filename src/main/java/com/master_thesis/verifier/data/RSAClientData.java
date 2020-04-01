package com.master_thesis.verifier.data;

import java.math.BigInteger;

public class RSAClientData extends ComputationData {

    private BigInteger proofComponent, publicKey;

    protected RSAClientData() {
        super(Construction.RSA);
    }

    public BigInteger getProofComponent() {
        return proofComponent;
    }

    public void setProofComponent(BigInteger proofComponent) {
        this.proofComponent = proofComponent;
    }

    public BigInteger getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(BigInteger publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public String toString() {
        return "RSAClientData{" +
                "proofComponent=" + proofComponent +
                ", publicKey=" + publicKey +
                "} " + super.toString();
    }
}
