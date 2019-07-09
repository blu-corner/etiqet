package com.neueda.etiqet.transport.jms.config;

import com.neueda.etiqet.core.config.xml.XmlParser;

public class JmsConfigXmlParser extends XmlParser {

    @Override
    protected String getSchemaFile() {
        return JmsConfigConstants.JMS_CONFIG_SCHEMA;
    }
}
