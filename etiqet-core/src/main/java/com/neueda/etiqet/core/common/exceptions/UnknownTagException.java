package com.neueda.etiqet.core.common.exceptions;

public class UnknownTagException extends EtiqetException {

	private static final long serialVersionUID = 1863433215161747633L;

	public UnknownTagException() {
		this.code = ErrorCodeType.ERROR_CODE_TAG_NOT_EXISTS;
	}
	
	public UnknownTagException(Throwable e) {
		super(e);
		this.code = ErrorCodeType.ERROR_CODE_TAG_NOT_EXISTS;
	}
	
	public UnknownTagException(String msg) {
		super(msg);
		this.code = ErrorCodeType.ERROR_CODE_TAG_NOT_EXISTS;
	}
	
	public UnknownTagException(String msg, Throwable e) {
		super(msg, e);
		this.code = ErrorCodeType.ERROR_CODE_TAG_NOT_EXISTS;
	}
}
