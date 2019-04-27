package com.neueda.etiqet.core.config.xml;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.config.dtos.Protocol;
import com.neueda.etiqet.core.util.StringUtils;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * XML Adapter for helping to unmarshall <code>&lt;protocol&gt;</code> elements in an Etiqet configuration file when
 * using the reference attribute
 */
public class ProtocolAdapter extends XmlAdapter<Protocol, Protocol> {

    /**
     * Unmarshalls {@link Protocol} elements. This checks for the existence of a reference attribute on the
     * <code>&lt;protocol&gt;</code> element which should reference a file containing the protocol to be parsed.
     *
     * @param protocolRef protocol object to be parsed
     * @return Messages object to form part of a protocol
     * @throws EtiqetException when unable to parse the Messages
     */
    @Override
    public Protocol unmarshal(Protocol protocolRef) throws EtiqetException {
        Protocol protocol;
        if (StringUtils.isNullOrEmpty(protocolRef.getReference())) {
            protocol = protocolRef;
        } else {
            protocol = new XmlParser().parse(protocolRef.getReference(), Protocol.class);
            protocol.setName(protocolRef.getName());
        }
        return protocol;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Not required for this adapter. Returns the argument to satisfy the {@link XmlAdapter} interface</p>
     *
     * @param protocol protocol implementation
     * @return protocol implementation
     */
    @Override
    public Protocol marshal(Protocol protocol) {
        return protocol;
    }

}
