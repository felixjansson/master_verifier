package com.master_thesis.verifier;

import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ClientBuffer {


    private Map<Integer, Substation> clientProofComponents;

    public ClientBuffer(){
        this.clientProofComponents = new HashMap<>();
    }

    public void removeFid(int substationID, int fid){
        clientProofComponents.get(substationID).remove(fid);
    }

    public void put(PartialClientInfo partialClientInfo){

        clientProofComponents.putIfAbsent(partialClientInfo.getSubstationID(), new Substation());
        Substation substation = clientProofComponents.get(partialClientInfo.getSubstationID());

        substation.putIfAbsent(partialClientInfo.getFid(), new Fid());
        Fid fidData = substation.get(partialClientInfo.getFid());
        fidData.put(partialClientInfo.getClientID(), partialClientInfo.getClientProofComponent());
    }

    public List<BigInteger> getClientProofs(int substationID, int fid){
        List<BigInteger> clientProofs = new ArrayList<>();
        if (clientProofComponents.containsKey(substationID)){
            clientProofs = clientProofComponents.get(substationID).getClientProofs(fid);
        }
        return clientProofs;
    }

    /**
     * Map of substationID and fid
     **/
    public class Substation extends HashMap<Integer, Fid>{

        public List<BigInteger> getClientProofs(int fid){
            if (containsKey(fid)){
                return new ArrayList<>(get(fid).values());
            }
            return new ArrayList<>();
        }
    }

    /**
     * Map of clientID and clientProofComponent
     **/
    public class Fid extends HashMap<Integer, BigInteger>{}



}
