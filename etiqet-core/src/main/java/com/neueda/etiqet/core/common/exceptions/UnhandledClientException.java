package com.neueda.etiqet.core.common.exceptions;

public class UnhandledClientException extends EtiqetException {

	private static final long serialVersionUID = -4101909952415232389L;

	public UnhandledClientException() {
		this.code = ErrorCodeType.ERROR_CODE_UNHANDLED_CLIENT;
	}
	
	public UnhandledClientException(Throwable e) {
		super(e);
		this.code = ErrorCodeType.ERROR_CODE_UNHANDLED_CLIENT;
	}
	
	public UnhandledClientException(String msg) {
		super(msg);
		this.code = ErrorCodeType.ERROR_CODE_UNHANDLED_CLIENT;
	}
	
	public UnhandledClientException(String msg, Throwable e) {
		super(msg, e);
		this.code = ErrorCodeType.ERROR_CODE_UNHANDLED_CLIENT;
	}
}
