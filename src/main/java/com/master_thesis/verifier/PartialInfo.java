package com.master_thesis.verifier;

import java.math.BigInteger;
import java.util.List;

public class PartialInfo {

    private BigInteger partialResult;
    private BigInteger serverPartialProof;
    private List<BigInteger> clientPartialProof;
    private int transformatorID;
    private int serverID;

    public BigInteger getPartialResult() {
        return partialResult;
    }

    public void setPartialResult(BigInteger partialResult) {
        this.partialResult = partialResult;
    }

    public BigInteger getServerPartialProof() {
        return serverPartialProof;
    }

    public void setServerPartialProof(BigInteger serverPartialProof) {
        this.serverPartialProof = serverPartialProof;
    }

    public List<BigInteger> getClientPartialProof() {
        return clientPartialProof;
    }

    public void setClientPartialProof(List<BigInteger> clientPartialProof) {
        this.clientPartialProof = clientPartialProof;
    }

    public int getTransformatorID() {
        return transformatorID;
    }

    public void setTransformatorID(int transformatorID) {
        this.transformatorID = transformatorID;
    }

    public int getServerID() {
        return serverID;
    }

    public void setServerID(int serverID) {
        this.serverID = serverID;
    }

    @Override
    public String toString() {
        return "PartialInfo{" +
                "partialResult=" + partialResult +
                ", serverPartialProof=" + serverPartialProof +
                ", clientPartialProof=" + clientPartialProof +
                ", transformatorID=" + transformatorID +
                ", serverID=" + serverID +
                '}';
    }
}
