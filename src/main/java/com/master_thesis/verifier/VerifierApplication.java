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
    private DataBuffer serverBuffer;
    private DataBuffer clientBuffer;
    private Lock bufferLock;
    private RSAThreshold rsaThresholdVerifier;
    private HomomorphicHash homomorphicHashVerifier;
    private LinearSignature linearSignature;
    private PublicParameters publicParameters;
    private NonceDistribution nonceDistribution;

    @Autowired
    public VerifierApplication(RSAThreshold rsaThresholdVerifier, HomomorphicHash homomorphicHashVerifier, LinearSignature linearSignature, NonceDistribution nonceDistribution, PublicParameters publicParameters) {
        this.nonceDistribution = nonceDistribution;
        this.serverBuffer = new DataBuffer();
        this.clientBuffer = new DataBuffer();
        this.bufferLock = new ReentrantLock();
        this.rsaThresholdVerifier = rsaThresholdVerifier;
        this.homomorphicHashVerifier = homomorphicHashVerifier;
        this.linearSignature = linearSignature;
        this.publicParameters = publicParameters;
    }

    @PostMapping(value = "/server/hash-data")
    public void receiveHashServerData(@RequestBody HashServerData serverData) throws InterruptedException {
        boolean isAllDataAvailable = putData(serverData, serverBuffer);
        if (isAllDataAvailable)
            new Thread(() -> performComputations(serverData.getSubstationID(), serverData.getFid())).start();
    }

    @PostMapping(value = "/client/hash-data")
    public void receiveHashClientData(@RequestBody HashClientData clientData) throws InterruptedException {
        boolean isAllDataAvailable = putData(clientData, clientBuffer);
        if (isAllDataAvailable)
            new Thread(() -> performComputations(clientData.getSubstationID(), clientData.getFid())).start();
    }

    @PostMapping(value = "/server/rsa-data")
    public void receiveRSAServerData(@RequestBody RSAServerData serverData) throws InterruptedException {
        boolean isAllDataAvailable = putData(serverData, serverBuffer);
        if (isAllDataAvailable)
            new Thread(() -> performComputations(serverData.getSubstationID(), serverData.getFid())).start();
    }

    @PostMapping(value = "/client/rsa-data")
    public void receiveRSAClientData(@RequestBody RSAClientData clientData) throws InterruptedException {
        boolean isAllDataAvailable = putData(clientData, clientBuffer);
        if (isAllDataAvailable)
            new Thread(() -> performComputations(clientData.getSubstationID(), clientData.getFid())).start();
    }

    @PostMapping(value = "/server/linear-data")
    public void receiveLinearServerData(@RequestBody LinearServerData serverData) throws InterruptedException {
        boolean isAllDataAvailable = putData(serverData, serverBuffer);
        if (isAllDataAvailable)
            new Thread(() -> performComputations(serverData.getSubstationID(), serverData.getFid())).start();
    }

    @PostMapping(value = "/client/linear-data")
    public void receiveLinearClientData(@RequestBody LinearClientData clientData) throws InterruptedException {
        boolean isAllDataAvailable = putData(clientData, clientBuffer);
        if (isAllDataAvailable)
            new Thread(() -> performComputations(clientData.getSubstationID(), clientData.getFid())).start();
    }

    @PostMapping(value = "/server/nonce-data")
    public void receiveLinearServerData(@RequestBody NonceServerData serverData) throws InterruptedException {
        boolean isAllDataAvailable = putData(serverData, serverBuffer);
        if (isAllDataAvailable)
            new Thread(() -> performComputations(serverData.getSubstationID(), serverData.getFid())).start();
    }

    @PostMapping(value = "/client/nonce-data")
    public void receiveLinearClientData(@RequestBody NonceClientData clientData) throws InterruptedException {
        boolean isAllDataAvailable = putData(clientData, clientBuffer);
        if (isAllDataAvailable)
            new Thread(() -> performComputations(clientData.getSubstationID(), clientData.getFid())).start();
    }


    private boolean putData(ComputationData data, DataBuffer buffer) throws InterruptedException {
        boolean isUnlocked = bufferLock.tryLock(1, TimeUnit.SECONDS);
        boolean canCompute = false;
        if (isUnlocked) {
            try {
                log.debug("Got {}", data);
                buffer.put(data);
                canCompute = canCompute(data.getSubstationID(), data.getFid());
            } finally {
                bufferLock.unlock();
            }
        }
        return canCompute;
    }

    private boolean canCompute(int substationID, int fid) {
        List<Integer> servers = publicParameters.getServers(); // TODO: 16/04/2020 Add substationID, fid
        List<Integer> clients = publicParameters.getClients(substationID, fid);
        if (serverBuffer.contains(substationID, fid) && clientBuffer.contains(substationID, fid)){
            boolean serverDataAvailable = serverBuffer.getFid(substationID, fid).keySet().containsAll(servers);
            boolean clientDataAvailable = clientBuffer.getFid(substationID, fid).keySet().containsAll(clients);
            return serverDataAvailable && clientDataAvailable;
        }
        return false;
    }


    private void performComputations(int substationID, int fid) {

        DataBuffer.Fid bufferServerData = serverBuffer.getFid(substationID, fid);
        DataBuffer.Fid bufferClientData = clientBuffer.getFid(substationID, fid);

        log.info("### Perform computation fid: {} Substation: {} Construction {}", fid, substationID, bufferServerData.getConstruction());

        // Homomorphic Hash verification
        if (bufferServerData.getConstruction().equals(Construction.HASH)) {
            List<HashServerData> serverData = bufferServerData.values().stream().map(val -> (HashServerData) val).collect(Collectors.toList());
            List<HashClientData> clientData = bufferClientData.values().stream().map(val -> (HashClientData) val).collect(Collectors.toList());
            performHomomorphicHashComputation(serverData, clientData, substationID, fid);
        }

        // RSA verification
        if (bufferServerData.getConstruction().equals(Construction.RSA)) {
            List<RSAServerData> serverData = bufferServerData.values().stream().map(val -> (RSAServerData) val).collect(Collectors.toList());
            List<RSAClientData> clientData = bufferClientData.values().stream().map(val -> (RSAClientData) val).collect(Collectors.toList());
            performRSAThresholdComputation(serverData, clientData, substationID, fid);
        }

        // Linear verification
        if (bufferServerData.getConstruction().equals(Construction.LINEAR)) {
            List<LinearServerData> serverData = bufferServerData.values().stream().map(val -> (LinearServerData) val).collect(Collectors.toList());
            List<LinearClientData> clientData = bufferClientData.values().stream().map(val -> (LinearClientData) val).collect(Collectors.toList());
            performLinearSignatureComputation(serverData, clientData, substationID, fid);
        }

        // Nonce verification
        if (bufferServerData.getConstruction().equals(Construction.NONCE)) {
            List<NonceServerData> serverData = bufferServerData.values().stream().map(val -> (NonceServerData) val).collect(Collectors.toList());
            List<NonceClientData> clientData = bufferClientData.values().stream().map(val -> (NonceClientData) val).collect(Collectors.toList());
            performNonceDistributionComputation(serverData, clientData, substationID, fid);
        }
        
    }

    private void performNonceDistributionComputation(List<NonceServerData> serverData, List<NonceClientData> clientData, int substationID, int fid) {
        List<BigInteger> clientProofs = clientData.stream().map(NonceClientData::getProofComponent).collect(Collectors.toList());
        BigInteger hashResult = nonceDistribution.finalEval(serverData.stream().map(NonceServerData::getPartialResult), substationID);
        BigInteger nonceResult = nonceDistribution.finalNonce(serverData.stream().map(NonceServerData::getPartialNonce), substationID);
        BigInteger hashServerProof = nonceDistribution.finalProof(serverData.stream().map(NonceServerData::getPartialProof), substationID);
        boolean hashValidResult = nonceDistribution.verify(substationID, hashResult, hashServerProof, clientProofs, nonceResult);
        log.info("[FID {}] Nonce: result:{} server proof:{} valid:{}", fid, hashResult, hashServerProof, hashValidResult);
    }

    private void performLinearSignatureComputation(List<LinearServerData> serverData, List<LinearClientData> clientData, int substationID, int fid) {
        BigInteger fieldBase = publicParameters.getFieldBase(substationID);
        BigInteger linearResult = linearSignature.finalEval(serverData.stream().map(LinearServerData::getPartialResult), fieldBase);
        LinearPublicData publicData = publicParameters.getLinearPublicData(substationID, fid);
        BigInteger rn = publicParameters.getRn(substationID, fid);
        LinearProofData proofData = linearSignature.finalProof(clientData, publicData);
        boolean validResult = linearSignature.verify(linearResult, proofData, publicData, rn);
        log.info("[FID {}] Linear: result:{} valid:{}", fid, linearResult, validResult);
    }

    private void performHomomorphicHashComputation(List<HashServerData> serverData, List<HashClientData> clientData, int substationID, int fid) {
        List<BigInteger> clientProofs = clientData.stream().map(HashClientData::getProofComponent).collect(Collectors.toList());
        BigInteger lastClientProof = publicParameters.getLastClientProof(substationID, fid);
        clientProofs.add(lastClientProof);
        BigInteger hashResult = homomorphicHashVerifier.finalEval(serverData.stream().map(HashServerData::getPartialResult), substationID);
        BigInteger hashServerProof = homomorphicHashVerifier.finalProof(serverData.stream().map(HashServerData::getPartialProof), substationID);
        boolean hashValidResult = homomorphicHashVerifier.verify(substationID, hashResult, hashServerProof, clientProofs);
        log.info("[FID {}] Hash: result:{} server proof:{} valid:{}", fid, hashResult, hashServerProof, hashValidResult);
    }


    private void performRSAThresholdComputation(List<RSAServerData> serverData, List<RSAClientData> clientData, int substationID, int fid) {
        List<BigInteger> clientProofs = clientData.stream().map(RSAClientData::getProofComponent).collect(Collectors.toList());
        BigInteger lastClientProof = publicParameters.getLastClientProof(substationID, fid);
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
