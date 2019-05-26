package com.neueda.etiqet.transport.qfj;

import com.neueda.etiqet.core.client.delegate.ClientDelegate;
import com.neueda.etiqet.core.common.Environment;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.common.exceptions.EtiqetRuntimeException;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.transport.Codec;
import com.neueda.etiqet.core.transport.Transport;
import com.neueda.etiqet.core.transport.TransportDelegate;
import com.neueda.etiqet.fix.client.delegate.FixClientDelegate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import quickfix.*;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

public class QfjTransport implements Transport, Application {

    private static final String CONFIG_CLIENT_LOGGON_TIMEOUT = "config.client.loggon.timeout";
    private static final int DEFAULT_CLIENT_LOGGON_TIMEOUT = 10;
    private static final Logger LOGGER = LogManager.getLogger(QfjTransport.class.getName());

    /* Delegate for processing messages during encoding and decoding phases */
    protected ClientDelegate delegate;
    private SocketInitiator socketInitiator;
    private TransportDelegate<String, Cdr> transDel;
    private Map<String, SessionID> sessionIds = new TreeMap<>();
    private Codec<Cdr, Message> codec;
    private int maxRetries;
    private SessionSettings sessionSettings;

    private static void replaceMessageFields(Message target, Message source) {
        // Replace/Add the fields from the source message
        source.iterator().forEachRemaining(f -> {
            final int fid = f.getField();
            if (fid == 10) {
                return;
            }

            if (target.getHeader().isSetField(fid)) {
                target.getHeader().removeField(fid);
                target.getHeader().setField(fid, f);
            } else if (target.getTrailer().isSetField(fid)) {
                target.getTrailer().setField(fid, f);
            } else if (target.isSetField(fid)) {
                target.removeField(fid);
                target.setField(fid, f);
            } else {
                target.setField(fid, f);
            }
        });
    }

    @Override
    public ClientDelegate getDelegate() {
        return delegate;
    }

    public void setDelegate(ClientDelegate del) {
        delegate = del;
    }

    @Override
    public void setTransportDelegate(TransportDelegate<String, Cdr> transDel) {
        this.transDel = transDel;
    }

    @Override
    public Codec getCodec() {
        return codec;
    }

    public void setCodec(Codec c) {
        codec = c;
    }

    @Override
    public void start() throws EtiqetException {
        try {
            socketInitiator.start();
            int i = 0;
            do {
                Thread.sleep(1000);
                i++;
            } while ((!isLoggedOn()) && (i < maxRetries));
        } catch (Exception e) {
            LOGGER.fatal("Error launching FIX client [" + e.getMessage() + "]", e);
            throw new EtiqetException("Could not start sessions. Reason: " + e.getCause(), e);
        }
    }

    @Override
    public void init(String configPath) {
        socketInitiator = null;
        try {
            sessionSettings = new SessionSettings(
                Environment.fileResolveEnvVars(configPath));
            maxRetries = sessionSettings.isSetting(CONFIG_CLIENT_LOGGON_TIMEOUT) ? sessionSettings
                .getInt(CONFIG_CLIENT_LOGGON_TIMEOUT) : DEFAULT_CLIENT_LOGGON_TIMEOUT;
            if (!new File(Environment.resolveEnvVars(configPath)).exists()) {
                LOGGER.fatal("Primary configuration not found [" + configPath + "]");
            }
            socketInitiator = new SocketInitiator(this,
                                                  new FileStoreFactory(sessionSettings), sessionSettings,
                                                  new LogAdapterFactory(LogAdapter.Level.INFO),
                                                  new DefaultMessageFactory());
        } catch (Exception exp) {
            LOGGER.fatal("Error initialising FIX client [" + exp.getMessage() + "]", exp);
            throw new EtiqetRuntimeException("Cannot initialise client. Reason: " + exp.getCause(), exp);
        }

    }

    @Override
    public void stop() {
        if (socketInitiator != null) {
            socketInitiator.stop();
        }
    }

    public boolean isLoggedOn() {
        return socketInitiator != null && socketInitiator.isLoggedOn();
    }

    @Override
    public String getDefaultSessionId() {
        return sessionIds.entrySet().iterator().next().getKey();
    }

    @Override
    public void send(Cdr msg) throws EtiqetException {
        send(msg, getDefaultSessionId());
    }

    @Override
    public void send(Cdr msg, String sessionId) throws EtiqetException {
        try {
            Session.sendToTarget(codec.encode(delegate.processMessage(msg)), sessionIds.get(sessionId));
        } catch (SessionNotFound sessionNotFound) {
            sessionNotFound.printStackTrace();
        }
    }

    // -----------------------------------------------------------------------------------------------
    // Application interface
    // -----------------------------------------------------------------------------------------------

    @Override
    public void onLogon(SessionID sessionID) {
        // Map the session ids and link the responders
        for (Session s : socketInitiator.getManagedSessions()) {
            s.setResponder(new InterceptorResponder(s.getResponder()));
            sessionIds.put(s.getSessionID().toString(), s.getSessionID());
        }
        // Delegate to application init after logon
        transDel.onLogon(sessionID.toString());
    }

    @Override
    public void onLogout(SessionID sessionID) {
        transDel.onLogout(sessionID.toString());
    }

    @Override
    public void onCreate(SessionID sessionId) {
        FixClientDelegate del = (FixClientDelegate) delegate.findDelegate(FixClientDelegate.class);
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
        transDel.onCreate(sessionId.toString());
    }

    @Override
    public void fromAdmin(Message msg, SessionID sessionID) {
        try {
            transDel.fromApp(delegate.processMessage(codec.decode(msg)), sessionID.toString());
        } catch (EtiqetException e) {
            throw new EtiqetRuntimeException("Error receiving admin message [" + msg.toString() + "]", e);
        }
    }

    @Override
    public void fromApp(Message msg, SessionID sessionID) {
        try {
            transDel.fromApp(delegate.processMessage(codec.decode(msg)), sessionID.toString());
        } catch (EtiqetException e) {
            throw new EtiqetRuntimeException("Error receiving app message [" + msg.toString() + "]", e);
        }
    }

    @Override
    public void toAdmin(Message msg, SessionID sessionID) {
        try {
            Cdr cdr = delegate.processMessage(codec.decode(msg));
            transDel.toApp(cdr, sessionID.toString());
            replaceMessageFields(msg, codec.encode(cdr));
        } catch (EtiqetException e) {
            throw new EtiqetRuntimeException("Error sending admin message [" + msg.toString() + "]", e);
        }
    }

    @Override
    public void toApp(Message msg, SessionID sessionID) {
        try {
            transDel.toApp(codec.decode(msg), sessionID.toString());
        } catch (EtiqetException e) {
            throw new EtiqetRuntimeException("Error sending app message [" + msg.toString() + "]", e);
        }
    }
}
