package com.neueda.etiqet.transport.amqp.config;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.config.xml.XmlParser;
import com.neueda.etiqet.transport.amqp.AmqpConfiguration;

public class AmqpConfigXmlParser extends XmlParser {

    public AmqpConfiguration parse(String xmlPath) throws EtiqetException {
        return parse(xmlPath, AmqpConfiguration.class);
    }

    @Override
    protected String getSchemaFile() {
        return AmqpConfigConstants.AMQP_CONFIG_SCHEMA;
    }
}
