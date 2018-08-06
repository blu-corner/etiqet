package com.neueda.etiqet.fix.client;

import com.neueda.etiqet.core.client.delegate.ClientDelegate;
import com.neueda.etiqet.core.client.delegate.SinkClientDelegate;
import com.neueda.etiqet.core.common.EtiqetEvent;
import com.neueda.etiqet.core.common.cdr.Cdr;
import com.neueda.etiqet.core.common.exceptions.EtiqetRuntimeException;
import com.neueda.etiqet.core.common.exceptions.StopEncodingException;
import com.neueda.etiqet.fix.client.delegate.FixClientDelegate;
import com.neueda.etiqet.fix.message.FIXUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import quickfix.*;

import java.util.concurrent.BlockingQueue;

public class FixApp extends MessageCracker implements Application {
    public static final Logger LOG = LogManager.getLogger(FixApp.class);

    private BlockingQueue<Cdr> sessionQueue;
    private BlockingQueue<Cdr> msgQueue;
    private SessionSettings sessionSettings;
    private final EtiqetEvent logonEvent;

    /* Delegate for processing messages during encoding and decoding phases */
    private ClientDelegate<Message, String> delegate;

    /**
     * Constructor.
     * @param sessionSettings session settings.
     * @param sessionQueue queue storing admin messages.
     * @param msgQueue queue storing app messages.
     * @param logonEvent sinchronisation event for logon.
     */

    public FixApp(SessionSettings sessionSettings, BlockingQueue<Cdr> sessionQueue,
                  BlockingQueue<Cdr> msgQueue, EtiqetEvent logonEvent) {
        this.sessionSettings = sessionSettings;
        this.sessionQueue = sessionQueue;
        this.msgQueue = msgQueue;
        this.logonEvent = logonEvent;
        delegate = new SinkClientDelegate<>();
    }

    /**
     * Sets the delegate to intercept the processing of messages during encoding and decoding phase.
     * @param delegate the delegates providing te processing of the messages during encoding and decoding phase.
     */
    public void setDelegate(ClientDelegate<Message, String> delegate) {
        this.delegate = delegate;
    }

    public void onCreate(SessionID sessionId) {
        LOG.info("Successfully called onCreate for sessionId : " + sessionId);
        FixClientDelegate del = (FixClientDelegate)delegate;
        try {
            del.init(sessionSettings.get(sessionId).getString("TargetSubID"),
                    sessionSettings.get(sessionId).getString("SenderSubID"),
                    sessionSettings.getString(sessionId, "Password"));
        } catch (ConfigError | FieldConvertError configError) {
            try {
                del.init(sessionSettings.get(sessionId).getString("TargetSubID"),
                        sessionSettings.get(sessionId).getString("SenderSubID"));
            } catch (Exception e) {
                throw new EtiqetRuntimeException(e);
            }
        }
    }

    public void fromAdmin(Message msg, SessionID sessionID) {
        receiveMsg(this.sessionQueue, msg);
    }

    public void fromApp(Message msg, SessionID sessionID) {
        receiveMsg(this.msgQueue, msg);
    }

    private void receiveMsg(BlockingQueue<Cdr> queue, Message msg) {
        try {
            Cdr cdr = new Cdr(msg.getHeader().getString(35));
            cdr.update(delegate.transformAfterReceiveMessage(FIXUtils.decode(delegate.transformAfterDecoding(msg))));
            queue.add(cdr);
        } catch (Exception e) {
            throw new EtiqetRuntimeException(e);
        }
    }

    public void onLogon(SessionID sessionID) {
        LOG.info("session logged on : " + sessionID);
        synchronized(logonEvent) {
            logonEvent.completeEvent();
            logonEvent.notifyAll();
        }
    }

    public void onLogout(SessionID sessionID) {
        LOG.info("session logged out : " + sessionID);
    }

    public void toAdmin(Message msg, SessionID sessionId) {
        try {
            delegate.transformBeforeEncoding(msg);
        } catch (StopEncodingException e) {
            throw new EtiqetRuntimeException(e);
        }
    }

    public void toApp(Message msg, SessionID sessionID) {
        try {
            delegate.transformBeforeEncoding(msg);
        } catch (StopEncodingException e) {
            throw new EtiqetRuntimeException(e);
        }
    }
}
