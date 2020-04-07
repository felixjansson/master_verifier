package com.master_thesis.verifier.data;

import java.math.BigInteger;

public class LinearClientData extends ComputationData {

    private BigInteger fidPrime;
    private BigInteger sShare;
    private BigInteger x;

    protected LinearClientData() {
        super(Construction.LINEAR);
    }


    public void setFidPrime(BigInteger fidPrime) {
        this.fidPrime = fidPrime;
    }

    public BigInteger getFidPrime() {
        return fidPrime;
    }

    public void setsShare(BigInteger sShare) {
        this.sShare = sShare;
    }

    public BigInteger getsShare() {
        return sShare;
    }

    public void setX(BigInteger x) {
        this.x = x;
    }

    public BigInteger getX() {
        return x;
    }


}
