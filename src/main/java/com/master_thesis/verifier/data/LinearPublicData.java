package com.master_thesis.verifier.data;

import java.math.BigInteger;

public class LinearPublicData {

    private BigInteger N, NRoof, fidPrime, g1, g2;
    private BigInteger[] h;

    public BigInteger getN() {
        return N;
    }

    public void setN(BigInteger n) {
        N = n;
    }

    public BigInteger getNRoof() {
        return NRoof;
    }

    public void setNRoof(BigInteger NRoof) {
        this.NRoof = NRoof;
    }

    public BigInteger getFidPrime() {
        return fidPrime;
    }

    public void setFidPrime(BigInteger fidPrime) {
        this.fidPrime = fidPrime;
    }

    public BigInteger getG1() {
        return g1;
    }

    public void setG1(BigInteger g1) {
        this.g1 = g1;
    }

    public BigInteger getG2() {
        return g2;
    }

    public void setG2(BigInteger g2) {
        this.g2 = g2;
    }

    public BigInteger[] getH() {
        return h;
    }

    public void setH(BigInteger[] h) {
        this.h = h;
    }


}
