package com.neueda.etiqet.fix.client.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.Log;

public class LogAdapter implements Log {

	public enum Level {
		TRACE, DEBUG, INFO, WARN, ERROR
	}

	private final Logger logger;
	private Level level;

	public LogAdapter(String context, Level level) {
		this.level = level;
		logger = LoggerFactory.getLogger("FIX ["+context+"]");
	}

	private void callLogger(String message) {
		switch (level) {
			case TRACE:
				logger.trace(message);
				break;

			case DEBUG:
				logger.debug(message);
				break;

			case INFO:
				logger.info(message);
				break;

			case WARN:
				logger.warn(message);
				break;

			case ERROR:
				logger.error(message);
				break;
		}
	}

	@Override
	public void clear() {
		/**
		 * Method required to fulfil implementation - however functionality is not required
		 */
	}

	@Override
	public void onErrorEvent(String text) {
		logger.error(text);
	}

	@Override
	public void onEvent(String text) {
		callLogger(text);
	}

	@Override
	public void onIncoming(String message) {
		callLogger("<< " + message);
	}

	@Override
	public void onOutgoing(String message) {
		callLogger(">> " + message);
	}

}
