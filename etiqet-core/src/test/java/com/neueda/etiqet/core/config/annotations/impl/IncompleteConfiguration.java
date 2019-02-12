package com.neueda.etiqet.core.config.annotations.impl;

import com.neueda.etiqet.core.config.annotations.Configuration;
import com.neueda.etiqet.core.config.annotations.EtiqetProtocol;
import com.neueda.etiqet.core.config.dtos.Protocol;

@Configuration
public class IncompleteConfiguration {

    @EtiqetProtocol("testProtocol")
    public Protocol getTestProtocol() {
        return new Protocol();
    }

}
