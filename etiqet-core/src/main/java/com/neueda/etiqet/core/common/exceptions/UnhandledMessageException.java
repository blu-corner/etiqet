package com.neueda.etiqet.core.common.exceptions;

/**
 * @author Neueda
 *
 */
public class UnhandledMessageException extends EtiqetException {

	private static final long serialVersionUID = -5857185419906974411L;
	
	public UnhandledMessageException() {
		this.code = ErrorCodeType.ERROR_CODE_UNHANDLED_MESSAGE;
	}
	
	public UnhandledMessageException(Throwable e) {
		super(e);
		this.code = ErrorCodeType.ERROR_CODE_UNHANDLED_MESSAGE;
	}
	
	public UnhandledMessageException(String msg) {
		super(msg);
		this.code = ErrorCodeType.ERROR_CODE_UNHANDLED_MESSAGE;
	}
	
	public UnhandledMessageException(String msg, Throwable e) {
		super(msg, e);
		this.code = ErrorCodeType.ERROR_CODE_UNHANDLED_MESSAGE;
	}

}
