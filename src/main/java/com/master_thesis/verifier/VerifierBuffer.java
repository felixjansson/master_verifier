package com.master_thesis.verifier;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class VerifierBuffer {

    private HashMap<Integer, Queue<PartialInfo>> map;

    PublicParameters publicParameters;
    private static final Logger log = (Logger) LoggerFactory.getLogger(VerifierBuffer.class);


    @Autowired
    public VerifierBuffer(PublicParameters publicParameters) {
        map = new HashMap<>();
        this.publicParameters = publicParameters;
        updateMap();
    }

    private void updateMap() {
        List<Integer> serverIDs = publicParameters.getServers();

        // Remove servers no longer active
        map.keySet().removeIf(Predicate.not(serverIDs::contains));

        //Add new
        serverIDs.forEach(serverId -> map.putIfAbsent(serverId, new LinkedList<>()));
    }

    public void put(PartialInfo partialInfo){
        map.putIfAbsent(partialInfo.getServerID(), new LinkedList<>());
        map.get(partialInfo.getServerID()).add(partialInfo);
    }

    public List<BigInteger> getServerPartialProofs(int id) {
        return map.values().stream()
                .map(Queue::peek)
                .filter(Objects::nonNull)
                .map(PartialInfo::getServerPartialProof)
                .collect(Collectors.toList());
    }

    public List<RsaProofComponent> getRSAProofComponents(int transformatorID) {
        List<List<RsaProofComponent>> rsaProofComponentList = map.values().stream()
                .map(Queue::peek)
                .filter(Objects::nonNull)
                .map(PartialInfo::getRsaProofComponents)
                .collect(Collectors.toList());
        List<RsaProofComponent> base = rsaProofComponentList.get(0);

        log.debug("this is RSA Proof list {}", rsaProofComponentList);
        if (rsaProofComponentList.stream().allMatch(base::containsAll)) {
            return base;
        } else {
            log.error("All rsaProofComponents do not match\n {}", rsaProofComponentList);
            throw new RuntimeException();
        }

    }

    public void pop() {
        map.values().forEach(Queue::remove);
    }

    public boolean canCompute() {
        updateMap();
        return map.values().stream().noneMatch(Queue::isEmpty);
    }

    public List<BigInteger> getClientProofs(int id) {
        List<List<BigInteger>> clientProofsList =  map.values()
                .stream()
                .map(Queue::peek)
                .filter(Objects::nonNull)
                .map(PartialInfo::getClientPartialProof)
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
        return map.values()
                .stream()
                .map(Queue::peek)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(PartialInfo::getServerID, PartialInfo::getPartialResult));
    }
}
