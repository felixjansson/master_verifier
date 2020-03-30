package com.master_thesis.verifier;

import java.math.BigInteger;
import java.util.Arrays;

public class ClientInfo {

    private BigInteger rsaN;
    private BigInteger clientProof;
    private BigInteger[] rsaProofComponent;
    private double rsaDeterminant;
    private BigInteger publicKey;


    public BigInteger getRsaN() {
        return rsaN;
    }

    public void setRsaN(BigInteger rsaN) {
        this.rsaN = rsaN;
    }

    public BigInteger getClientProof() {
        return clientProof;
    }

    public void setClientProof(BigInteger clientProof) {
        this.clientProof = clientProof;
    }

    public BigInteger[] getRsaProofComponent() {
        return rsaProofComponent;
    }

    public void setRsaProofComponent(BigInteger[] rsaProofComponent) {
        this.rsaProofComponent = rsaProofComponent;
    }

    public double getRsaDeterminant() {
        return rsaDeterminant;
    }

    public void setRsaDeterminant(double rsaDeterminant) {
        this.rsaDeterminant = rsaDeterminant;
    }

    public BigInteger getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(BigInteger publicKey) {
        this.publicKey = publicKey;
    }


    @Override
    public String toString() {
        return "ClientInfo{" +
                "rsaN=" + rsaN +
                ", clientProof=" + clientProof +
                ", rsaProofComponent=" + Arrays.toString(rsaProofComponent) +
                ", rsaDeterminant=" + rsaDeterminant +
                ", publicKey=" + publicKey +
                '}';
    }
}
