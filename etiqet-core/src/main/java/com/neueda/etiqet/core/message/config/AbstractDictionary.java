package com.neueda.etiqet.core.message.config;

import com.neueda.etiqet.core.common.exceptions.UnknownTagException;

import java.io.Serializable;

public abstract class AbstractDictionary implements Serializable {

    protected final String configPath;

	public AbstractDictionary(String configPath) {
        this.configPath = configPath;
	}
	
	public abstract String getMsgType(String messageName);

    public abstract String getMsgName(String messageType);

    public abstract String getNameForTag(Integer tag);

    public abstract Integer getTagForName(String n) throws UnknownTagException;

    public abstract boolean tagContains(Integer tag);

    public abstract boolean isHeaderField(String fieldName);

    public abstract boolean isHeaderField(Integer tag);

    public abstract boolean isAdmin(String messageName);
}
