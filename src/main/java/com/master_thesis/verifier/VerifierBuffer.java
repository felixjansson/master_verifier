package com.master_thesis.verifier;

import ch.qos.logback.classic.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class VerifierBuffer {

    private HashMap<Integer, Queue<PartialInfo>> map;

    PublicParameters publicParameters;

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

    public List<Integer> getPartialProofs() {
        return map.values().stream()
                .map(Queue::peek)
                .filter(Objects::nonNull)
                .map(PartialInfo::getPartialProof)
                .collect(Collectors.toList());
    }

    public List<Integer> getPartialResults() {
        return map.values().stream()
                .map(Queue::peek)
                .filter(Objects::nonNull)
                .map(PartialInfo::getPartialResult)
                .collect(Collectors.toList());
    }

    public void pop() {
        map.values().forEach(Queue::remove);
    }

    public boolean canCompute() {
        updateMap();
        return map.values().stream().noneMatch(Queue::isEmpty);
    }

}
