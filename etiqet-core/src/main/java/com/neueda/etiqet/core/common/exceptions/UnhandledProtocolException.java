package com.neueda.etiqet.core.common.exceptions;

public class UnhandledProtocolException extends EtiqetException {

	private static final long serialVersionUID = 1153969725702821779L;

	public UnhandledProtocolException() {
		this.code = ErrorCodeType.ERROR_CODE_UNHANDLED_PROTOCOL;
	}
	
	public UnhandledProtocolException(Throwable e) {
		super(e);
		this.code = ErrorCodeType.ERROR_CODE_UNHANDLED_PROTOCOL;
	}
	
	public UnhandledProtocolException(String msg) {
		super(msg);
		this.code = ErrorCodeType.ERROR_CODE_UNHANDLED_PROTOCOL;
	}
	
	public UnhandledProtocolException(String msg, Throwable e) {
		super(msg, e);
		this.code = ErrorCodeType.ERROR_CODE_UNHANDLED_PROTOCOL;
	}
}
