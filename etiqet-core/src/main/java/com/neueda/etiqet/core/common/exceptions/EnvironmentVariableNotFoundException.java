package com.neueda.etiqet.core.common.exceptions;

public class EnvironmentVariableNotFoundException extends EtiqetException {

	private static final long serialVersionUID = 3143878033946266147L;

	public EnvironmentVariableNotFoundException() {
		this.code = ErrorCodeType.ERROR_CODE_ENV_VAR;
	}
	
	public EnvironmentVariableNotFoundException(Throwable e) {
		super(e);
		this.code = ErrorCodeType.ERROR_CODE_ENV_VAR;
	}
	
	public EnvironmentVariableNotFoundException(String msg) {
		super(msg);
		this.code = ErrorCodeType.ERROR_CODE_ENV_VAR;
	}
	
	public EnvironmentVariableNotFoundException(String msg, Throwable e) {
		super(msg, e);
		this.code = ErrorCodeType.ERROR_CODE_ENV_VAR;
	}
}
