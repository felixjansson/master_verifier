package com.master_thesis.verifier.data;

public abstract class ComputationData {

    private final Construction construction;
    private int fid, substationID, id;

    protected ComputationData(Construction construction) {
        this.construction = construction;
    }

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public int getSubstationID() {
        return substationID;
    }

    public void setSubstationID(int substationID) {
        this.substationID = substationID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Construction getConstruction() {
        return construction;
    }

    @Override
    public String toString() {
        return "ComputationData{" +
                "fid=" + fid +
                ", substationID=" + substationID +
                ", id=" + id +
                ", construction=" + construction +
                '}';
    }
}
