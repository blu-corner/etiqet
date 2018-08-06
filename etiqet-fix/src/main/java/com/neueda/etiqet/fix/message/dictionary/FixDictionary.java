package com.neueda.etiqet.fix.message.dictionary;

import com.neueda.etiqet.core.common.Environment;
import com.neueda.etiqet.core.common.exceptions.UnknownTagException;
import com.neueda.etiqet.core.message.config.AbstractDictionary;
import com.neueda.etiqet.core.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FixDictionary extends AbstractDictionary {
    
	private static final Logger LOG = LogManager.getLogger(FixDictionary.class);

    private Map<Integer, String> names;

	private Map<String, Integer> tags;
	
	private transient Map<String, Field> headerFieldsMap;
	
	private transient Map<String, Message> messageMap;
	
	private transient Map<String, Message> messageMapByType;

	public FixDictionary(String dictionaryPath) {
		super(dictionaryPath);

		try {
			JAXBContext jaxbContext;

			jaxbContext = JAXBContext.newInstance(Fix.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Fix dictionary = (Fix) jaxbUnmarshaller.unmarshal(new File(Environment.resolveEnvVars(dictionaryPath)));

			names = new HashMap<>();
			tags = new HashMap<>();
			for (Field field : dictionary.getFields().getField()) {
				names.put(field.getNumber(), field.getName());
				tags.put(field.getName(), field.getNumber());
			}
			
			messageMap = new HashMap<>();
			messageMapByType = new HashMap<>();
			for (Message message: dictionary.getMessages().getMessage()) {
				messageMap.put(message.getName(), message);
				messageMapByType.put(message.getMsgtype(), message);
			}
			
			headerFieldsMap = new HashMap<>();
			if (dictionary.getHeader() != null
					&& dictionary.getHeader().getField() != null && dictionary.getHeader().getField().length > 0) {
				for (Field field: dictionary.getHeader().getField()) {
					headerFieldsMap.put(field.getName(), field);
				}
			}
		} catch (Exception e) {
			String msg = "Unable to load protocol definition file: " + dictionaryPath ;
			LOG.error(msg, e);
		}
	}

	@Override
	public String getMsgType(String messageName) {
		String msgType = null;
		Message message = messageMap.get(messageName);
		if (message != null) {
			msgType = message.getMsgtype();	
		}		
		return msgType;
	}
	
	@Override
	public String getMsgName(String messageType) {
		String msgName = null;
		Message message = messageMapByType.get(messageType);
		if (message != null) {
			msgName = message.getName();	
		}		
		return msgName;
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
		if (!tags.containsKey(n))
			throw new UnknownTagException("failed to find tag for " + n);

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
		boolean isHeaderField = false;
		String fieldName = names.get(tag);
		if (!StringUtils.isNullOrEmpty(fieldName)) {
			isHeaderField = headerFieldsMap.containsKey(fieldName);
		}
		return isHeaderField;
	}
	
	public boolean isAdmin(String messageName) {
		boolean out = false;
		Message message = messageMap.get(messageName);
		if (message != null) {
			out = "admin".equals(message.getMsgcat());
		}
		return out;
	}
}
