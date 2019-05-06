package com.neueda.etiqet.transport.qfj;

import com.neueda.etiqet.transport.qfj.LogAdapter.Level;
import quickfix.Log;
import quickfix.LogFactory;
import quickfix.SessionID;

/**
 * LogFactory to create log4j compatible QF loggers.
 *
 * @author Neueda
 */
public class LogAdapterFactory implements LogFactory {

    private Level level;

    public LogAdapterFactory(Level level) {
        this.level = level;
    }

    @Override
    public Log create(SessionID sessionID) {
        return new LogAdapter(sessionID.toString(), level);
    }

}
