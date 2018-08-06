package com.neueda.etiqet.core.common.exceptions;

public class SerializeException extends EtiqetException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9101746318064899723L;

	public SerializeException() {
		this.code = ErrorCodeType.ERROR_CODE_SERIALIZE;
	}
	
	public SerializeException(Throwable e) {
		super(e);
		this.code = ErrorCodeType.ERROR_CODE_SERIALIZE;
	}
	
	public SerializeException(String msg) {
		super(msg);
		this.code = ErrorCodeType.ERROR_CODE_SERIALIZE;
	}
	
	public SerializeException(String msg, Throwable e) {
		super(msg, e);
		this.code = ErrorCodeType.ERROR_CODE_SERIALIZE;
	}
}
