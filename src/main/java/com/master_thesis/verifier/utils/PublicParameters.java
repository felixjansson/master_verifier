package com.master_thesis.verifier.utils;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.master_thesis.verifier.HomomorphicHash;
import com.master_thesis.verifier.data.LinearPublicData;
import lombok.SneakyThrows;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Component
public class PublicParameters {

    private final static Logger log = (Logger) LoggerFactory.getLogger(PublicParameters.class);
    private static ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public List<Integer> getServers() {

        HttpRequest httpRequest = HttpRequest.newBuilder(URI.create("http://localhost:4000/api/server/list/ids"))
                .GET().build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());
        List<Integer> servers = objectMapper.readValue(response.body(), new TypeReference<>() {
        });
        return servers;
    }

    @SneakyThrows
    public BigInteger getFieldBase(int substationID) {
        URI uri = URI.create("http://localhost:4000/api/setup/fieldBase/" + substationID);
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return new BigInteger(response.body());
    }

    @SneakyThrows
    public BigInteger getGenerator(int substationID) {
        URI uri = URI.create("http://localhost:4000/api/setup/generator/" + substationID);
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return new BigInteger(response.body());
    }

    @SneakyThrows
    public BigInteger getLastClientProof(int substationID, int fid) {
        URI uri = URI.create(
                String.format("http://localhost:4000/lastClient/%d/%d/computeLastTau",
                        substationID, fid
                ));
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return new BigInteger(response.body());
    }

    @SneakyThrows
    public LinearPublicData getLinearPublicData(int substationID, int fid) {
        URI uri = URI.create(
                String.format("http://localhost:4000/api/linear-data/public/%d/%d",
                        substationID, fid
                ));
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), LinearPublicData.class);
    }

    @SneakyThrows
    public BigInteger getRn(int substationID, int fid) {
        URI uri = URI.create(
                String.format("http://localhost:4000/api/linear-data/rn/%d/%d",
                        substationID, fid
                ));
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        try{
            return new BigInteger(response.body());
        } catch (NumberFormatException e) {
            log.error("Could not parse BigInteger from response: {}", response.body());
            throw e;
        }

    }

    @SneakyThrows
    public List<Integer> getClients(int substationID, int fid) {
        URI uri = URI.create("http://localhost:4000/api/client/list/" + substationID + "/" + fid);
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), List.class);
    }
}
