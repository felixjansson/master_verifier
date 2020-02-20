package com.master_thesis.verifier;

public class PartialInfo {

    private int partialProof;

    private int partialEval;

    private int serverId;

    public int getPartialProof() {
        return partialProof;
    }

    public void setPartialProof(int partialProof) {
        this.partialProof = partialProof;
    }

    public int getPartialEval() {
        return partialEval;
    }

    public void setPartialEval(int partialEval) {
        this.partialEval = partialEval;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    @Override
    public String toString() {
        return "PartialInfo{" +
                "partialProof=" + partialProof +
                ", partialEval=" + partialEval +
                ", serverId=" + serverId +
                '}';
    }
}
