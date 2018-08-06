package com.neueda.etiqet.core.common.exceptions;

import java.util.List;

/**
 * 
 * @author Neueda
 *
 */
public class ValidationException extends EtiqetException {

	private static final long serialVersionUID = -82798913678757214L;
	private final transient List<Object> list;

	public ValidationException(List<Object> list) {
		this.code = ErrorCodeType.ERROR_CODE_FIELD_VALIDATION;
		this.list = list;
	}
	
	public ValidationException(Throwable e, List<Object> list) {
		super(e);
		this.code = ErrorCodeType.ERROR_CODE_FIELD_VALIDATION;
		this.list = list;
	}

	public List<Object> getList() {
		return list;
	}

}
