package com.neueda.etiqet.fix.message;

import com.neueda.etiqet.core.common.cdr.Cdr;
import com.neueda.etiqet.core.common.cdr.CdrItem;
import com.neueda.etiqet.core.common.cdr.CdrItem.CdrItemType;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.common.exceptions.SerializeException;
import com.neueda.etiqet.core.common.exceptions.UnknownTagException;
import com.neueda.etiqet.core.config.GlobalConfig;
import com.neueda.etiqet.core.message.config.ProtocolConfig;
import com.neueda.etiqet.core.util.IntegerValidator;
import com.neueda.etiqet.core.util.ParserUtils;
import com.neueda.etiqet.fix.config.FixConfigConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import quickfix.*;

import java.util.HashMap;

public class FIXMsg {
	private static final Logger LOG = LogManager.getLogger(FIXMsg.class);

	private FieldMap instance;
	
	public FIXMsg() {
	}

	public FIXMsg(Cdr d, FieldMap instance) {
		this.instance = instance;
		try {
			create(d);
		} catch (Exception lex) {
			LOG.error("Error creating a FIXMsg");
		}
	}
	
	public void create(Cdr data) throws EtiqetException {
		populateMessage(data);
	}

	private void populateMessage(Cdr data) throws EtiqetException {
		ProtocolConfig protocolConfig = GlobalConfig.getInstance().getProtocol(FixConfigConstants.PROTOCOL_NAME);
		data.clear();
		com.neueda.etiqet.core.config.dtos.Message messageConfig = protocolConfig.getMessage(data.getType());
		if (messageConfig != null) {
			ParserUtils.fillDefault(messageConfig, data);
		}
	}

	public Message serialize(Cdr cdr) throws EtiqetException {
		try {		
			ProtocolConfig protocolConfig = GlobalConfig.getInstance().getProtocol(FixConfigConstants.PROTOCOL_NAME);
			com.neueda.etiqet.core.config.dtos.Message messageConfig = protocolConfig.getMessage(cdr.getType());
			instance = (Message) Class.forName(messageConfig.getImplementation()).getConstructor().newInstance();
			this.encode(cdr);
		} catch (EtiqetException e) {
			LOG.error(e);
			throw e;
		} catch (Exception e) {
			LOG.error(e);
			throw new SerializeException(e);
		}

		return (Message) instance;
	}

	private int getIntegerTag(ProtocolConfig protocolConfig, String key) throws UnknownTagException {
		int tag;
		if (IntegerValidator.isParseable(key)) {
			tag = IntegerValidator.tryParse(key);
			if (!protocolConfig.tagContains(tag)) {
				throw new UnknownTagException("failed to find tag for " + key);
			}
		} else {
			tag = protocolConfig.getTagForName(key);
		}
		return tag;
	}

	/**
	 * Encode a msg based on the message type which it is an instance of
	 * @param protocolConfig
	 * @param tag
	 * @param entry
	 * @throws UnknownTagException
	 */
	private void encodeKnownMsg(
	    ProtocolConfig protocolConfig,
        Integer tag,
        HashMap.Entry<String, CdrItem> entry
    ) throws UnknownTagException {
		// Determine field type and assign correctly
		boolean isHeaderField = protocolConfig.isHeaderField(tag);
		switch (entry.getValue().getType()) {
			case CDR_INTEGER:
				IntField intf = new IntField(tag);
				intf.setValue(entry.getValue().getIntval());
				if (isHeaderField) {
					((Message) instance).getHeader().setField(intf);
				} else {
					instance.setField(intf);
				}
				break;
			case CDR_DOUBLE:
				DoubleField df = new DoubleField(tag);
				df.setValue(entry.getValue().getDoubleval());
				if (isHeaderField) {
					((Message) instance).getHeader().setField(df);
				} else {
					instance.setField(df);
				}
				break;
			case CDR_STRING:
				StringField sf = new StringField(tag, entry.getValue().getStrval());
				if (isHeaderField) {
					((Message) instance).getHeader().setField(sf);
				} else {
					instance.setField(sf);
				}
				break;
			default:
				throw new UnknownTagException();
		}
	}

	/**
	 * Encode msg of type Array
	 * @param protocolConfig protocol configuration to populate the entry from
	 * @param entry entry to populate the values from
	 * @throws EtiqetException When the MessageComponent couldn't be instantiated
	 */
	private void encodeArrayType(
		ProtocolConfig protocolConfig,
		HashMap.Entry<String, CdrItem> entry
	) throws EtiqetException {
        String className = protocolConfig.getComponentPackage() + entry.getKey();
        MessageComponent msg = null;
        try {
            msg = (MessageComponent) Class.forName(className).getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new EtiqetException(String.format("Could not create message component %s", className), e);
        }
        for (Cdr cdr2 : entry.getValue().getCdrs()) {
			FIXMsg component = new FIXMsg(cdr2, msg);
			component.instance = msg;
			try {
				msg.setFields(component.serialize(cdr2));
			} catch (EtiqetException e) {
				LOG.error("Error serializing component: " + cdr2.toString());
			}
		}
		instance.setFields(msg);
	}

	/**
	 * Encode msg based on field types
	 * @param entry
	 * @param tag
	 * @throws UnknownTagException
	 */
	private void encodeUnknownMsg(HashMap.Entry<String, CdrItem> entry, Integer tag) throws UnknownTagException {
		switch (entry.getValue().getType()) {
			case CDR_INTEGER:
				IntField intf = new IntField(tag);
				intf.setValue(entry.getValue().getIntval());
				instance.setField(intf);
				break;
			case CDR_DOUBLE:
				DoubleField df = new DoubleField(tag);
				df.setValue(entry.getValue().getDoubleval());
				instance.setField(df);
				break;
			case CDR_STRING:
				instance.setField(new StringField(tag, entry.getValue().getStrval()));
				break;

			default:
				throw new UnknownTagException();
		}
	}

	/**
	 * Encodes abstract fields to fix class fields
     * @param cdr cdr object to be encoded
	 * @throws EtiqetException if something went wrong during the encoding.
	 */
	private void encode(Cdr cdr) throws EtiqetException {
		ProtocolConfig protocolConfig = GlobalConfig.getInstance().getProtocol(FixConfigConstants.PROTOCOL_NAME);
		for (HashMap.Entry<String, CdrItem> entry : cdr.getItems().entrySet()) {
			String key = entry.getKey();
			if (entry.getValue().getType() != CdrItemType.CDR_ARRAY) {
				// Get tag
				Integer tag=getIntegerTag(protocolConfig,key);

				if (instance instanceof Message) {
					// Determine field type and assign correctly
					encodeKnownMsg(protocolConfig,tag,entry);
				} else {
					// Determine field type and assign correctly
					encodeUnknownMsg(entry,tag);
				}
			} else {
				// TYPE CDR_ARRAY
				encodeArrayType(protocolConfig,entry);
			}
		}
	}
}
