package com.master_thesis.verifier.data;

import ch.qos.logback.classic.Logger;
import com.master_thesis.verifier.utils.PublicParameters;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class DataBuffer {

    private static final Logger log = (Logger) LoggerFactory.getLogger(DataBuffer.class);

    private HashMap<Integer, Substation> substations;

    public DataBuffer() {
        substations = new HashMap<>();
    }

    public void put(ComputationData serverData) {
        substations.putIfAbsent(serverData.getSubstationID(), new Substation());
        Substation substation = substations.get(serverData.getSubstationID());
        substation.putIfAbsent(serverData.getFid(), new Fid(serverData.getConstruction()));
        Fid fidData = substation.get(serverData.getFid());
        fidData.put(serverData.getId(), serverData);
    }

    public Fid getFid(int substationID, int fid) {
        return substations.get(substationID).get(fid);
    }

    public boolean contains(int substationID, int fid) {
        return substations.containsKey(substationID) && substations.get(substationID).containsKey(fid);
    }

    private class Substation extends HashMap<Integer, Fid> {
    }

    public class Fid extends HashMap<Integer, ComputationData> {

        private Construction construction;

        public Fid(Construction construction) {
            this.construction = construction;
        }

        public Construction getConstruction() {
            return construction;
        }
    }
}
