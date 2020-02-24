package com.master_thesis.verifier;

public class PartialInfo {

    private int partialProof;

    private int partialResult;

    private int serverID;

    public int getPartialProof() {
        return partialProof;
    }

    public void setPartialProof(int partialProof) {
        this.partialProof = partialProof;
    }

    public int getPartialResult() {
        return partialResult;
    }

    public void setPartialResult(int partialResult) {
        this.partialResult = partialResult;
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
                "partialProof=" + partialProof +
                ", partialResult=" + partialResult +
                ", serverId=" + serverID +
                '}';
    }
}
