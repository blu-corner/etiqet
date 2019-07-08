package com.neueda.etiqet.sql.fixture;

public class DbServerNotFoundException extends RuntimeException {
    public DbServerNotFoundException() {
        super();
    }

    public DbServerNotFoundException(String message) {
        super(message);
    }
}
