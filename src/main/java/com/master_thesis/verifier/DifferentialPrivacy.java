package com.master_thesis.verifier;

import com.master_thesis.verifier.utils.PublicParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DifferentialPrivacy extends HomomorphicHash {

    @Autowired
    public DifferentialPrivacy(PublicParameters publicParameters) {
        super(publicParameters);
    }

}