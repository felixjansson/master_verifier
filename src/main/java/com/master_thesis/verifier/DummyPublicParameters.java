package com.master_thesis.verifier;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DummyPublicParameters implements PublicParameters{

    @Override
    public List<Integer> getServers() {



        return Stream.of(1, 2).collect(Collectors.toList());
    }

}
