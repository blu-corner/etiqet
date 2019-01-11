package com.neueda.etiqet.transport.qfj;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.common.exceptions.SerializeException;
import com.neueda.etiqet.core.common.exceptions.UnknownTagException;
import com.neueda.etiqet.core.config.GlobalConfig;
import com.neueda.etiqet.core.message.Cdr;
import com.neueda.etiqet.core.message.CdrItem;
import com.neueda.etiqet.core.message.CdrItem.CdrItemType;
import com.neueda.etiqet.core.message.config.ProtocolConfig;
import com.neueda.etiqet.core.transport.Codec;
import com.neueda.etiqet.core.util.IntegerValidator;
import com.neueda.etiqet.core.util.ParserUtils;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import quickfix.DoubleField;
import quickfix.Field;
import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.IntField;
import quickfix.Message;
import quickfix.MessageComponent;
import quickfix.StringField;
import quickfix.field.MsgType;

public class QfjCodec implements Codec<Cdr, Message> {

  private static final Logger logger = LogManager.getLogger(QfjCodec.class);
  private ProtocolConfig protocolConfig;

  public QfjCodec() throws EtiqetException {
    protocolConfig = GlobalConfig.getInstance().getProtocol("fix");
  }

  /**
   * Encode a msg based on the message type which it is an instance of
   */
  private void encodeKnownMsg(Integer tag, HashMap.Entry<String, CdrItem> entry, FieldMap message)
      throws UnknownTagException {
    // Determine field type and assign correctly
    boolean isHeaderField = protocolConfig.isHeaderField(tag);
    switch (entry.getValue().getType()) {
      case CDR_INTEGER:
        IntField intf = new IntField(tag);
        intf.setValue(entry.getValue().getIntval().intValue());
        if (isHeaderField) {
          ((Message) message).getHeader().setField(intf);
        } else {
          message.setField(intf);
        }
        break;
      case CDR_DOUBLE:
        DoubleField df = new DoubleField(tag);
        df.setValue(entry.getValue().getDoubleval());
        if (isHeaderField) {
          ((Message) message).getHeader().setField(df);
        } else {
          message.setField(df);
        }
        break;
      case CDR_STRING:
        StringField sf = new StringField(tag, entry.getValue().getStrval());
        if (isHeaderField) {
          ((Message) message).getHeader().setField(sf);
        } else {
          message.setField(sf);
        }
        break;
      default:
        throw new UnknownTagException();
    }
  }

  /**
   * Encode msg of type Array
   *
   * @param entry entry to populate the values from
   * @throws EtiqetException When the MessageComponent couldn't be instantiated
   */
  private void encodeArrayType(HashMap.Entry<String, CdrItem> entry,
      FieldMap message) throws EtiqetException {
    String className = protocolConfig.getComponentPackage() + entry.getKey();
    try {
      MessageComponent msg = (MessageComponent) Class.forName(className).getConstructor()
          .newInstance();
      for (Cdr cdr2 : entry.getValue().getCdrs()) {
        msg.setFields(encode(cdr2, message));
      }
      message.setFields(msg);
    } catch (ReflectiveOperationException e) {
      throw new EtiqetException(String.format("Could not create message component %s", className),
          e);
    }
  }

  /**
   * Encode msg based on field types
   */
  private void encodeUnknownMsg(HashMap.Entry<String, CdrItem> entry, Integer tag, FieldMap message)
      throws UnknownTagException {
    switch (entry.getValue().getType()) {
      case CDR_INTEGER:
        IntField intf = new IntField(tag);
        intf.setValue(entry.getValue().getIntval().intValue());
        message.setField(intf);
        break;
      case CDR_DOUBLE:
        DoubleField df = new DoubleField(tag);
        df.setValue(entry.getValue().getDoubleval());
        message.setField(df);
        break;
      case CDR_STRING:
        message.setField(new StringField(tag, entry.getValue().getStrval()));
        break;

      default:
        throw new UnknownTagException();
    }
  }


  private int getIntegerTag(String key) throws UnknownTagException {
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


  private Message encode(Cdr cdr, FieldMap message) throws EtiqetException {
    for (HashMap.Entry<String, CdrItem> entry : cdr.getItems().entrySet()) {
      String key = entry.getKey();

      // Tags prefixed with - should be ignored
      if (key.charAt(0) == '-') {
        logger.info("Ignoring field [" + getIntegerTag(key.substring(1)) + "]");
        continue;
      }

      if (entry.getValue().getType() != CdrItemType.CDR_ARRAY) {
        // Get tag
        Integer tag = getIntegerTag(key);

        if (message instanceof Message) {
          // Determine field type and assign correctly
          encodeKnownMsg(tag, entry, message);
        } else {
          // Determine field type and assign correctly
          encodeUnknownMsg(entry, tag, message);
        }
      } else {
        // TYPE CDR_ARRAY
        encodeArrayType(entry, message);
      }
    }
    return (Message) message;
  }

  @Override
  public Message encode(Cdr cdr) throws EtiqetException {
    try {
      com.neueda.etiqet.core.config.dtos.Message messageConfig =
          protocolConfig.getMessage(cdr.getType());
      ParserUtils.fillDefault(messageConfig, cdr);
      return encode(cdr, (Message) Class.forName(messageConfig.getImplementation()).getConstructor()
          .newInstance());
    } catch (Exception e) {
      logger.error(e);
      throw new SerializeException(e);
    }
  }

  @Override
  public Cdr decode(Message msg) throws EtiqetException {
    try {
      Cdr d = new Cdr(protocolConfig.getMsgName(msg.getHeader().getString(MsgType.FIELD)));
      Iterator<Field<?>> itr = msg.getHeader().iterator();
      while (itr.hasNext()) {
        StringField f = (StringField) itr.next();
        d.set(protocolConfig.getNameForTag(f.getTag()), f.getValue());
      }

      itr = msg.iterator();
      while (itr.hasNext()) {
        StringField f = (StringField) itr.next();
        d.set(protocolConfig.getNameForTag(f.getTag()), f.getValue());
      }

      itr = msg.getTrailer().iterator();
      while (itr.hasNext()) {
        StringField f = (StringField) itr.next();
        d.set(protocolConfig.getNameForTag(f.getTag()), f.getValue());
      }
      return d;
    } catch (FieldNotFound fieldNotFound) {
      throw new EtiqetException(fieldNotFound);
    }
  }
}

