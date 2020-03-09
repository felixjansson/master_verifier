package com.master_thesis.verifier;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

public class DatabaseConnection {

    private final static Logger log = (Logger) LoggerFactory.getLogger(DatabaseConnection.class);

    public static void put(BigInteger proof, BigInteger result, boolean isValid) {
        log.info("writing to DB success ish");
    }


}
