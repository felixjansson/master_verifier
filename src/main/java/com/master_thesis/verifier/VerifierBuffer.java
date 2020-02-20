package com.master_thesis.verifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class VerifierBuffer {

    private Map<Integer, Queue<PartialInfo>> map;
    PublicParameters publicParameters;

    @Autowired
    public VerifierBuffer(PublicParameters publicParameters) {
        map = new HashMap<>();
        this.publicParameters = publicParameters;
        updateMap();
    }

    private void updateMap() {
        publicParameters.getServers().forEach(serverId -> map.putIfAbsent(serverId, new LinkedList<>()));
    }

    public void put(PartialInfo partialInfo){
        map.putIfAbsent(partialInfo.getServerId(), new LinkedList<>());
        map.get(partialInfo.getServerId()).add(partialInfo);
    }

    public List<Integer> getPartialProofs() {
        return map.values().stream()
                .map(Queue::poll)
                .filter(Objects::nonNull)
                .map(PartialInfo::getPartialProof)
                .collect(Collectors.toList());
    }

    public List<Integer> getPartialEvals() {
        return map.values().stream()
                .map(Queue::poll)
                .filter(Objects::nonNull)
                .map(PartialInfo::getPartialEval)
                .collect(Collectors.toList());
    }

    public boolean canCompute() {
        updateMap();
        return map.values().stream().noneMatch(Queue::isEmpty);
    }

}
