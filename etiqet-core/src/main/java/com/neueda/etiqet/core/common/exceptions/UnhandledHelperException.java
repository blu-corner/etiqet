package com.neueda.etiqet.core.common.exceptions;

public class UnhandledHelperException extends EtiqetException {

	private static final long serialVersionUID = 2926167203177627407L;

	public UnhandledHelperException() {
		this.code = ErrorCodeType.ERROR_CODE_UNHANDLED_HELPER;
	}
	
	public UnhandledHelperException(Throwable e) {
		super(e);
		this.code = ErrorCodeType.ERROR_CODE_UNHANDLED_HELPER;
	}
	
	public UnhandledHelperException(String msg) {
		super(msg);
		this.code = ErrorCodeType.ERROR_CODE_UNHANDLED_HELPER;
	}
	
	public UnhandledHelperException(String msg, Throwable e) {
		super(msg, e);
		this.code = ErrorCodeType.ERROR_CODE_UNHANDLED_HELPER;
	}
}
