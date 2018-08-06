package com.neueda.etiqet.fix.message;

import com.neueda.etiqet.core.common.cdr.Cdr;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.config.GlobalConfig;
import com.neueda.etiqet.core.message.config.ProtocolConfig;
import com.neueda.etiqet.fix.config.FixConfigConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import quickfix.Field;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.StringField;
import quickfix.field.MsgType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Random;

public class FIXUtils {

	public static final Logger LOG = LogManager.getLogger(FIXUtils.class);

	public static String getDateTime() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm:ss.SSS");
		LocalDateTime now = LocalDateTime.now();

		return dtf.format(now);
	}

	public static String genClientOrderID() {

		Random r = new Random();
		Integer randomNumber = r.nextInt(10000) + 1;

		return getDateTime() + randomNumber.toString();
	}

	public static Message encode(Cdr cdr) throws EtiqetException {
		return new FIXMsg().serialize(cdr);
	}

	public static Cdr decode(Message msg) throws EtiqetException {
		try {
			Cdr d = new Cdr(msg.getHeader().getString(MsgType.FIELD));
			Iterator<Field<?>> itr = msg.getHeader().iterator();
			ProtocolConfig protocolConfig = GlobalConfig.getInstance().getProtocol(FixConfigConstants.PROTOCOL_NAME);
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
