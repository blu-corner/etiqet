package com.neueda.etiqet.core.config.xml;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.config.dtos.Messages;
import com.neueda.etiqet.core.util.StringUtils;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * XML Adapter for helping to unmarshall <code>&lt;messages&gt;</code> elements in an Etiqet configuration file when
 * using the reference attribute
 */
public class MessagesAdapter extends XmlAdapter<Messages, Messages> {

    /**
     * Unmarshalls {@link Messages} elements. This checks for the existence of a reference attribute on the
     * <code>&lt;messages&gt;</code> element which should reference a file containing messages for the protocol
     * currently being parsed.
     *
     * @param ref Messages object to be parsed
     * @return Messages object to form part of a protocol
     * @throws EtiqetException when unable to parse the Messages
     */
    @Override
    public Messages unmarshal(Messages ref) throws EtiqetException {
        Messages messages;
        if (StringUtils.isNullOrEmpty(ref.getReference())) {
            messages = ref;
        } else {
            XmlParser parser = new XmlParser();
            messages = parser.parse(ref.getReference(), Messages.class);
        }
        return messages;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Not required for this adapter. Returns the argument to satisfy the {@link XmlAdapter} interface</p>
     *
     * @param messages messages implementation
     * @return messages implementation
     */
    @Override
    public Messages marshal(Messages messages) {
        return messages;
    }

}
