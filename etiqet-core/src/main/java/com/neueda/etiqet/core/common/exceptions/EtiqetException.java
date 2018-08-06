package com.neueda.etiqet.core.common.exceptions;

/**
 * 
 * @author Neueda
 *
 */
public class EtiqetException extends Exception {

	private static final long serialVersionUID = 7363363183129767718L;

	protected ErrorCodeType code;

	public EtiqetException() {}
	
	public EtiqetException(String msg) {
		super(msg);
	}
	
	public EtiqetException(Throwable e) {
		super(e);
	}
	
	public EtiqetException(String msg, Throwable e) {
		super(msg, e);
	}
	
	public enum ErrorCodeType {
		ERROR_CODE_OK(0),
		ERROR_CODE_FIELD_VALIDATION(-100),
		ERROR_CODE_UNHANDLED_MESSAGE(-200),
		ERROR_CODE_UNHANDLED_HELPER(-300),
		ERROR_CODE_UNHANDLED_PROTOCOL(-400),
		ERROR_CODE_UNHANDLED_CLIENT(-500),
		ERROR_CODE_TAG_NOT_EXISTS(-600),
		ERROR_CODE_SERIALIZE(-700), 
		ERROR_CODE_ENV_VAR(-800);
		
		private int code;

		private ErrorCodeType(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}
		
	}
	
	public ErrorCodeType getCode() {
		return code;
	}

}
