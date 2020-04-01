package com.master_thesis.verifier;

import ch.qos.logback.classic.Logger;
import com.master_thesis.verifier.data.*;
import com.master_thesis.verifier.utils.PublicParameters;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
@RestController
@RequestMapping(value = "/api")
public class VerifierApplication {

    private static final Logger log = (Logger) LoggerFactory.getLogger(VerifierApplication.class);
    private ServerBuffer serverBuffer;
    private ClientBuffer clientBuffer;
    private Lock bufferLock;
    private RSAThreshold rsaThresholdVerifier;
    private HomomorphicHash homomorphicHashVerifier;
    private PublicParameters publicParameters;

    @Autowired
    public VerifierApplication(ServerBuffer serverBuffer, ClientBuffer clientBuffer, RSAThreshold rsaThresholdVerifier, HomomorphicHash homomorphicHashVerifier, PublicParameters publicParameters) {
        this.serverBuffer = serverBuffer;
        this.clientBuffer = clientBuffer;
        this.rsaThresholdVerifier = rsaThresholdVerifier;
        this.homomorphicHashVerifier = homomorphicHashVerifier;
        this.publicParameters = publicParameters;
        this.bufferLock = new ReentrantLock();
    }

    @PostMapping(value = "/server/hash-data")
    public void receiveHashServerData(@RequestBody HashServerData serverData) throws InterruptedException {
        boolean isUnlocked = bufferLock.tryLock(1, TimeUnit.SECONDS);
        if (isUnlocked) {
            try {
                log.debug("Got {}", serverData);
                serverBuffer.put(serverData);
                if (serverBuffer.canCompute(serverData.getSubstationID(), serverData.getFid()))
                    new Thread(() -> performComputations(serverData.getSubstationID(), serverData.getFid())).start();
            } finally {
                bufferLock.unlock();
            }
        }
    }

    @PostMapping(value = "/server/rsa-data")
    public void receiveRSAServerData(@RequestBody RSAServerData serverData) throws InterruptedException {
        boolean isUnlocked = bufferLock.tryLock(1, TimeUnit.SECONDS);
        if (isUnlocked) {
            try {
                log.debug("Got {}", serverData);
                serverBuffer.put(serverData);
                if (serverBuffer.canCompute(serverData.getSubstationID(), serverData.getFid()))
                    new Thread(() -> performComputations(serverData.getSubstationID(), serverData.getFid())).start();
            } finally {
                bufferLock.unlock();
            }
        }
    }

    @PostMapping(value = "/client/hash-data")
    void receiveHashClientData(@RequestBody HashClientData clientData) {
        log.debug("Got {}", clientData);
        clientBuffer.put(clientData);
    }


    @PostMapping(value = "/client/rsa-data")
    void receiveRSAClientData(@RequestBody RSAClientData clientData) {
        log.debug("Got {}", clientData);
        clientBuffer.put(clientData);
    }

    private void performComputations(int substationID, int fid) {

        ServerBuffer.Fid fidData = serverBuffer.getFid(substationID, fid);
        BigInteger lastClientProof = publicParameters.getLastClientProof(substationID, fid);

        // Homomorphic Hash verification
        if (fidData.getConstruction().equals(Construction.HASH)) {
            List<HashServerData> serverData = fidData.values().stream().map(val -> (HashServerData) val).collect(Collectors.toList());
            List<HashClientData> clientData = clientBuffer.getFid(substationID, fid).values().stream().map(val -> (HashClientData) val).collect(Collectors.toList());
            performHomomorphicHashComputation(serverData, clientData, lastClientProof, substationID, fid);
        }

        // RSA verification
        if (fidData.getConstruction().equals(Construction.RSA)) {
            List<RSAServerData> serverData = fidData.values().stream().map(val -> (RSAServerData) val).collect(Collectors.toList());
            List<RSAClientData> clientData = clientBuffer.getFid(substationID, fid).values().stream().map(val -> (RSAClientData) val).collect(Collectors.toList());
            performRSAThresholdComputation(serverData, clientData, lastClientProof, substationID, fid);
        }

    }

    private void performHomomorphicHashComputation(List<HashServerData> serverData, List<HashClientData> clientData, BigInteger lastClientProof, int substationID, int fid) {
        List<BigInteger> clientProofs = clientData.stream().map(HashClientData::getProofComponent).collect(Collectors.toList());
        clientProofs.add(lastClientProof);
        BigInteger hashResult = homomorphicHashVerifier.finalEval(serverData.stream().map(HashServerData::getPartialResult), substationID);
        BigInteger hashServerProof = homomorphicHashVerifier.finalProof(serverData.stream().map(HashServerData::getPartialProof), substationID);
        boolean hashValidResult = homomorphicHashVerifier.verify(substationID, hashResult, hashServerProof, clientProofs);
        log.info("[FID {}] Hash: result:{} server proof:{} valid:{}", fid, hashResult, hashServerProof, hashValidResult);
    }


    private void performRSAThresholdComputation(List<RSAServerData> serverData, List<RSAClientData> clientData, BigInteger lastClientProof, int substationID, int fid) {
        List<BigInteger> clientProofs = clientData.stream().map(RSAClientData::getProofComponent).collect(Collectors.toList());
        clientProofs.add(lastClientProof);
        Stream<BigInteger> partialResults = serverData.stream().map(RSAServerData::getPartialResult);
        Map<Integer, RSAServerData.ProofData> serverProofInfo = serverData.get(0).getPartialProofs();

        // Add the public key to each proof component computation.
        clientData.forEach(client -> serverProofInfo.get(client.getId()).setPublicKey(client.getPublicKey()));

        BigInteger rsaResult = rsaThresholdVerifier.finalEval(partialResults, substationID);
        BigInteger rsaServerProof = rsaThresholdVerifier.finalProof(serverProofInfo.values(), substationID, lastClientProof);
        boolean rsaValidResult = rsaThresholdVerifier.verify(substationID, rsaResult, rsaServerProof, clientProofs);

        log.info("[FID {}] RSA: result:{} server proof:{} valid:{}", fid, rsaResult, rsaServerProof, rsaValidResult);
    }


    public static void main(String[] args) {
        SpringApplication.run(VerifierApplication.class, args);
    }

}
