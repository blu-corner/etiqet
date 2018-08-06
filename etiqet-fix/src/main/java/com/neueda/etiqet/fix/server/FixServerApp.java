package com.neueda.etiqet.fix.server;

import com.neueda.etiqet.core.common.cdr.Cdr;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.common.exceptions.UnknownTagException;
import com.neueda.etiqet.core.config.GlobalConfig;
import com.neueda.etiqet.core.message.Codec;
import com.neueda.etiqet.core.message.config.ProtocolConfig;
import com.neueda.etiqet.fix.config.FixConfigConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import quickfix.*;
import quickfix.field.MsgType;

public class FixServerApp extends MessageCracker implements Application, Codec<Message> {

	public static final Logger LOG = LogManager.getLogger(FixServerApp.class);

	private ProtocolConfig protocolConfig;

	public FixServerApp() throws EtiqetException {
        protocolConfig = GlobalConfig.getInstance().getProtocol(FixConfigConstants.PROTOCOL_NAME);
    }

	@Override
	public void fromAdmin(Message arg0, SessionID arg1) {
		LOG.info("Server: fromAdmin " + arg0);
	}

	/**
	 * Method to handle incoming messages from client. 
	 * @param message incoming message.
	 * @param sessionID session identifier.
	 * @throws UnsupportedMessageType exception throw when a messagetype is not allowed.
	 */
	@Override
	public void fromApp(Message message, SessionID sessionID) throws UnsupportedMessageType {
		handleMessage(message, sessionID);
	}
	
	/**
	 * Method that process input message and return a response.
	 * @param input message to process.
	 * @throws UnsupportedMessageType exception thrown when the input message is not handled.
	 */
	private void handleMessage(Message input, SessionID sessionID) throws UnsupportedMessageType {
		LOG.debug("type of message: " + input.getClass().getSimpleName());
		String classSimpleName = input.getClass().getSimpleName();
		if (classSimpleName.equalsIgnoreCase("NewOrderSingle")){
			handleNewOrderSingle(input, sessionID);
		} else {
		    throw new UnsupportedMessageType();
		}
	}
	
	/**
	 * Method to handle NewOrderSingle message.
	 * @param input newOrderSingle message
	 */
	private void handleNewOrderSingle(Message input, SessionID sessionID) {
		Message response;
		try {
			response = getExecutionReport(input);
			Session.sendToTarget(response, sessionID);
		} catch (SessionNotFound e) {
			LOG.error("Session not found: " + sessionID.toString(), e);
		}
	}
	
	/**
	 * Method to create a Execution Report message
	 * @param input base
	 * @return an execution Report message.
	 */
	private Message getExecutionReport(Message input) {
		Message msg = null;
		try {
			String clOrdId = input.getString(protocolConfig.getTagForName("ClOrdID"));
			Cdr cdr = new Cdr(MsgType.ORDER_SINGLE);
			cdr.set("ClOrdID", clOrdId);
			msg = encode(cdr);
		} catch (UnknownTagException e) {
			LOG.error("Unknown Tag", e);
		} catch (FieldNotFound e) {
			LOG.error("Field Not Found", e);
		} catch (EtiqetException e) {
			LOG.error("General Exception", e);
		}
		return msg;
	}

	/**
	 * Method to handle session creation.
	 */
	@Override
	public void onCreate(SessionID sessionID) {
		LOG.info("Server: onCreate " + sessionID.toString());
	}

	/**
	 * Method to handle when a client tries to create logged on.
	 * @param sessionID identifier of session.
	 */
	@Override
	public void onLogon(SessionID sessionID) {
		LOG.info("Server: onLogon of "+ sessionID);
	}
	
	@Override
	public void onLogout(SessionID sessionID) {
		LOG.info("Server onLogout " + sessionID.toString());
	}

	@Override
	public void toAdmin(Message message, SessionID sessionID) {
		LOG.info("Server toAdmin sessionId: " + sessionID.toString() + " , message: " + message.toString());
	}

	@Override
	public void toApp(Message message, SessionID sessionID) throws DoNotSend {
		LOG.info("Server toApp sessionId: " + sessionID.toString() + " , message: " + message.toString());
	}

	@Override
	public Message encode(Cdr message) throws EtiqetException {
		return null;
	}

	@Override
	public Cdr decode(Message message) throws EtiqetException {
		return null;
	}

}
