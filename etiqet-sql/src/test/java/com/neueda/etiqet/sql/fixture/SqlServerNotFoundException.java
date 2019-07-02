package com.neueda.etiqet.sql.fixture;

public class SqlServerNotFoundException extends RuntimeException {
    public SqlServerNotFoundException() {
        super();
    }

    public SqlServerNotFoundException(String message) {
        super(message);
    }
}
