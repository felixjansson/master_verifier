package com.master_thesis.verifier.data;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ClientBuffer {


    private Map<Integer, Substation> substations;

    public ClientBuffer(){
        this.substations = new HashMap<>();
    }

    public void removeFid(int substationID, int fid){
        substations.get(substationID).remove(fid);
    }

    public void put(ComputationData clientData) {

        substations.putIfAbsent(clientData.getSubstationID(), new Substation());
        Substation substation = substations.get(clientData.getSubstationID());

        substation.putIfAbsent(clientData.getFid(), new Fid());
        Fid fidData = substation.get(clientData.getFid());
        fidData.put(clientData.getId(), clientData);
    }

    public Fid getFid(int substationID, int fid) {
        return substations.get(substationID).get(fid);
    }

    /**
     * Map of substationID and fid
     **/
    public static class Substation extends HashMap<Integer, Fid> {
    }

    /**
     * Map of clientID and clientProofComponent
     **/
    public static class Fid extends HashMap<Integer, ComputationData> {
    }


}
