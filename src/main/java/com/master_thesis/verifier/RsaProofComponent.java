package com.master_thesis.verifier;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;

public class RsaProofComponent {

    private BigInteger pk;
    private BigInteger clientProof;
    private BigInteger[] serverProofs;
    private BigInteger rsaN;
    private BigInteger determinant;

    public BigInteger getPk() {
        return pk;
    }

    public void setPk(BigInteger pk) {
        this.pk = pk;
    }

    public BigInteger getClientProof() {
        return clientProof;
    }

    public void setClientProof(BigInteger clientProof) {
        this.clientProof = clientProof;
    }

    public BigInteger[] getServerProofs() {
        return serverProofs;
    }

    public void setServerProofs(BigInteger[] serverProofs) {
        this.serverProofs = serverProofs;
    }

    public BigInteger getRsaN() {
        return rsaN;
    }

    public void setRsaN(BigInteger rsaN) {
        this.rsaN = rsaN;
    }

    public BigInteger getDeterminant() {
        return determinant;
    }

    public void setDeterminant(BigInteger determinant) {
        this.determinant = determinant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RsaProofComponent that = (RsaProofComponent) o;
        return Objects.equals(pk, that.pk) &&
                Objects.equals(clientProof, that.clientProof) &&
                Arrays.equals(serverProofs, that.serverProofs) &&
                Objects.equals(rsaN, that.rsaN) &&
                Objects.equals(determinant, that.determinant);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(pk, clientProof, rsaN, determinant);
        result = 31 * result + Arrays.hashCode(serverProofs);
        return result;
    }

    @Override
    public String toString() {
        return "RsaProofComponent{" +
                "PK=" + pk +
                ", clientProof=" + clientProof +
                ", serverProofs=" + Arrays.toString(serverProofs) +
                ", rsaN=" + rsaN +
                ", determinant=" + determinant +
                '}';
    }
}
