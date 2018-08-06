package com.neueda.etiqet.core.testing.message;

import com.neueda.etiqet.core.common.exceptions.UnknownTagException;
import com.neueda.etiqet.core.message.config.AbstractDictionary;

public class TestDictionary extends AbstractDictionary {

    public TestDictionary(String configPath) {
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
        return tag.toString();
    }

    @Override
    public Integer getTagForName(String n) throws UnknownTagException {
        return 0;
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
