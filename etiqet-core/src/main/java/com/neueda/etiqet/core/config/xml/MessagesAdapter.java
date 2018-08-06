package com.neueda.etiqet.core.config.xml;

import com.neueda.etiqet.core.config.dtos.Messages;
import com.neueda.etiqet.core.util.StringUtils;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class MessagesAdapter extends XmlAdapter<Messages, Messages> {

    @Override
    public Messages unmarshal(Messages ref) throws Exception {
        Messages messages;
        if(StringUtils.isNullOrEmpty(ref.getReference())) {
            messages = ref;
        } else {
            XmlParser parser = new XmlParser();
            messages = parser.parse(ref.getReference(), Messages.class);
        }
        return messages;
    }

    @Override
    public Messages marshal(Messages messages) throws Exception {
        return messages;
    }

}
