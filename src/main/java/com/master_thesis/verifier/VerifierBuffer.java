package com.master_thesis.verifier;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class VerifierBuffer {

    private static final Logger log = (Logger) LoggerFactory.getLogger(VerifierBuffer.class);

    PublicParameters publicParameters;
    private HashMap<Integer, Substation> substations;


    @Autowired
    public VerifierBuffer(PublicParameters publicParameters) {
        substations = new HashMap<>();
        this.publicParameters = publicParameters;
    }

    public void put(PartialInfo partialInfo) {
        substations.putIfAbsent(partialInfo.getSubstationID(), new Substation());
        Substation substation = substations.get(partialInfo.getSubstationID());
        substation.putIfAbsent(partialInfo.getFid(), new Fid());
        Fid fidData = substation.get(partialInfo.getFid());
        fidData.put(partialInfo.getServerID(), partialInfo);
    }

    public Fid getFid(int substationID, int fid) {
        return substations.get(substationID).get(fid);
    }


    public boolean canCompute(int substationID, int fid) { // TODO: 2020-02-24 Check with PP how many clients
        List<Integer> clientIDs = publicParameters.getServers();
        return substations.get(substationID).get(fid).keySet().containsAll(clientIDs);
    }

    private class Substation extends HashMap<Integer, Fid> {
    }

    public class Fid extends HashMap<Integer, PartialInfo> {

        public List<BigInteger> getShares() {
            return values().stream().map(PartialInfo::getHomomorphicPartialProof).collect(Collectors.toList());
        }

        public List<ClientInfo> getRSAProofComponents() {
            List<ClientInfo[]> rsaProofComponentList = values().stream()
                    .map(PartialInfo::getClientInfos)
                    .collect(Collectors.toList());
            ClientInfo[] base = rsaProofComponentList.get(0);
            return Arrays.asList(base);
        }

        public List<BigInteger> getClientProofs() {
            List<List<BigInteger>> clientProofsList = values()
                    .stream()
                    .map(PartialInfo::getClientInfos)
                    .map((ClientInfo[] t) ->
                            Arrays.stream(t)
                                    .map(ClientInfo::getClientProof)
                                    .collect(Collectors.toList()))
                    .collect(Collectors.toList());
            List<BigInteger> base = clientProofsList.get(0);

            log.debug("This is client list - {}", clientProofsList);

            if (clientProofsList.stream().allMatch(base::containsAll)) {
                return base;
            } else {
                log.error("All ClientProofList do not match\n {}", clientProofsList);
                throw new RuntimeException();
            }
        }

        public Map<Integer, BigInteger> getPartialResultsInfo(int id) {
            return values()
                    .stream()
                    .collect(Collectors.toMap(PartialInfo::getServerID, PartialInfo::getPartialResult));
        }

    }

}
