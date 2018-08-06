package com.neueda.etiqet.core.config.xml;

import com.neueda.etiqet.core.config.dtos.Protocol;
import com.neueda.etiqet.core.util.StringUtils;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ProtocolAdapter extends XmlAdapter<Protocol, Protocol> {

    @Override
    public Protocol unmarshal(Protocol protocolRef) throws Exception {
        Protocol protocol;
        if(StringUtils.isNullOrEmpty(protocolRef.getReference())) {
            protocol = protocolRef;
        } else {
            protocol = new XmlParser().parse(protocolRef.getReference(), Protocol.class);
            protocol.setName(protocolRef.getName());
        }
        return protocol;
    }

    @Override
    public Protocol marshal(Protocol protocol) throws Exception {
        return protocol;
    }

}
