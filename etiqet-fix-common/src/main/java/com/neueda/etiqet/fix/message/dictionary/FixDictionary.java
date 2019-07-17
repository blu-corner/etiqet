package com.neueda.etiqet.fix.message.dictionary;

import com.neueda.etiqet.core.common.Environment;
import com.neueda.etiqet.core.common.exceptions.UnknownTagException;
import com.neueda.etiqet.core.message.config.AbstractDictionary;
import com.neueda.etiqet.core.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FixDictionary extends AbstractDictionary {

    private static final Logger LOG = LoggerFactory.getLogger(FixDictionary.class);

    private Map<Integer, String> names;

    private Map<String, Integer> tags;

    private transient Map<String, Field> headerFieldsMap;

    private transient Map<String, Message> messageMap;

    private transient Map<String, Message> messageMapByType;

    private Fix dictionary;

    public FixDictionary(String dictionaryPath) {
        super(dictionaryPath);

        try {
            Unmarshaller jaxbUnmarshaller = JAXBContext.newInstance(Fix.class).createUnmarshaller();
            dictionary = (Fix) jaxbUnmarshaller.unmarshal(new File(Environment.resolveEnvVars(dictionaryPath)));

            names = new HashMap<>();
            tags = new HashMap<>();
            for (Field field : dictionary.getFields().getField()) {
                names.put(field.getNumber(), field.getName());
                tags.put(field.getName(), field.getNumber());
            }

            messageMap = new HashMap<>();
            messageMapByType = new HashMap<>();
            for (Message message : dictionary.getMessages().getMessage()) {
                messageMap.put(message.getName(), message);
                messageMapByType.put(message.getMsgtype(), message);
            }

            headerFieldsMap = new HashMap<>();
            if (dictionary.getHeader() != null
                && dictionary.getHeader().getField() != null && dictionary.getHeader().getField().length > 0) {
                for (Field field : dictionary.getHeader().getField()) {
                    headerFieldsMap.put(field.getName(), field);
                }
            }
        } catch (Exception e) {
            LOG.error("Unable to load protocol definition file: {}", dictionaryPath, e);
        }
    }

    public Fix getFixDictionary() {
        return dictionary;
    }

    @Override
    public String getMsgType(String messageName) {
        Message message = messageMap.get(messageName);
        if (message != null) {
            return message.getMsgtype();
        }
        return null;
    }

    @Override
    public String getMsgName(String messageType) {
        Message message = messageMapByType.get(messageType);
        if (message != null) {
            return message.getName();
        }
        return null;
    }

    @Override
    public String getNameForTag(Integer tag) {
        if (!names.containsKey(tag)) {
            return tag.toString();
        }
        return names.get(tag);
    }

    @Override
    public Integer getTagForName(String n) throws UnknownTagException {
        if (!tags.containsKey(n)) {
            throw new UnknownTagException("failed to find tag for " + n);
        }

        return tags.get(n);
    }

    @Override
    public boolean tagContains(Integer tag) {
        return names.containsKey(tag);
    }

    @Override
    public boolean isHeaderField(String fieldName) {
        return headerFieldsMap.containsKey(fieldName);
    }

    @Override
    public boolean isHeaderField(Integer tag) {
        String fieldName = names.get(tag);
        if (!StringUtils.isNullOrEmpty(fieldName)) {
            return headerFieldsMap.containsKey(fieldName);
        }
        return false;
    }

    @Override
    public boolean isAdmin(String messageName) {
        Message message = messageMap.get(messageName);
        if (message != null) {
            return "admin".equals(message.getMsgcat());
        }
        return false;
    }
}
