package com.neueda.etiqet.db.fixture;

public class DbServerNotFoundException extends RuntimeException {
    public DbServerNotFoundException() {
        super();
    }

    public DbServerNotFoundException(String message) {
        super(message);
    }
}
