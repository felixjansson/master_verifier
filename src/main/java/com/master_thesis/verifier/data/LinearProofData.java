package com.master_thesis.verifier.data;

import java.math.BigInteger;
import java.util.List;

public class LinearProofData {


    private BigInteger s;
    private BigInteger xTilde;
    private List<BigInteger> f;

    public LinearProofData(BigInteger s, BigInteger xTilde, List<BigInteger> f) {

        this.s = s;
        this.xTilde = xTilde;
        this.f = f;
    }

    public BigInteger getS() {
        return s;
    }

    public BigInteger getXTilde() {
        return xTilde;
    }

    public List<BigInteger> getF() {
        return f;
    }
}
