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

    private static final Logger log = (Logger) LoggerFactory.getLogger(VerifierBuffer.class);
    PublicParameters publicParameters;
    private HashMap<Integer, Queue<PartialInfo>> map;


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

    public void put(PartialInfo partialInfo) {
        map.putIfAbsent(partialInfo.getServerID(), new LinkedList<>());
        map.get(partialInfo.getServerID()).add(partialInfo);
    }

    public List<ClientInfo> getRSAProofComponents(int transformatorID) {
        List<ClientInfo[]> rsaProofComponentList = map.values().stream()
                .map(Queue::peek)
                .filter(Objects::nonNull)
                .map(PartialInfo::getClientInfos)
                .collect(Collectors.toList());
        ClientInfo[] base = rsaProofComponentList.get(0);
        return Arrays.asList(base);
    }

    public void pop() {
        map.values().stream().filter(p -> !p.isEmpty()).forEach(Queue::remove); // TODO: 2020-03-12 remove bad fault tolerance
    }

    public boolean canCompute() {
        updateMap();
        return map.values().stream().noneMatch(Queue::isEmpty);
    }

    public List<BigInteger> getClientProofs(int id) {
        List<List<BigInteger>> clientProofsList = map.values()
                .stream()
                .map(Queue::peek)
                .filter(Objects::nonNull)
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
        return map.values()
                .stream()
                .map(Queue::peek)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(PartialInfo::getServerID, PartialInfo::getPartialResult));
    }
}
