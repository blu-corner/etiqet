package com.neueda.etiqet.rest.message;

import com.neueda.etiqet.core.common.exceptions.UnknownTagException;
import com.neueda.etiqet.core.message.config.AbstractDictionary;

/**
 * Stub dictionary to handle the message name / type for comparing results
 */
public class RestDictionary extends AbstractDictionary {

    public RestDictionary(String configPath) {
        super(null);
    }

    @Override
    public String getMsgType(String messageName) {
        return messageName;
    }

    @Override
    public String getMsgName(String messageType) {
        return messageType;
    }

    @Override
    public String getNameForTag(Integer tag) {
        return null;
    }

    @Override
    public Integer getTagForName(String n) throws UnknownTagException {
        return null;
    }

    @Override
    public boolean tagContains(Integer tag) {
        return false;
    }

    @Override
    public boolean isHeaderField(String fieldName) {
        return false;
    }

    @Override
    public boolean isHeaderField(Integer tag) {
        return false;
    }

    @Override
    public boolean isAdmin(String messageName) {
        return false;
    }

}
