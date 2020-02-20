package com.master_thesis.verifier;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DummyPublicParameters implements PublicParameters{

    @Override
    @SneakyThrows
    public List<Integer> getServers() {

        HttpRequest httpRequest = HttpRequest.newBuilder(URI.create("http://localhost:4000/api/server/list"))
                .GET().build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());
        List<ServerData> servers = new ObjectMapper().readValue(response.body(), new TypeReference<>() {});
        return servers.stream().map(ServerData::getServerID).collect(Collectors.toList());
    }

}
