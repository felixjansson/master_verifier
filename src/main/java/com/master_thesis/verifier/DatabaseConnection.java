package com.master_thesis.verifier;

import ch.qos.logback.classic.Logger;
import lombok.SneakyThrows;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DatabaseConnection {

    private final static Logger log = (Logger) LoggerFactory.getLogger(DatabaseConnection.class);

    @SneakyThrows
    public static void put(BigInteger proof, BigInteger result, boolean isValid) {
        log.info("writing to DB success ish");
    }


}
