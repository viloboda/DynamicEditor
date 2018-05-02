package com.example.dal;

public class DataContextException extends Exception {
    public DataContextException(Exception e) {
        super(e);
    }

    public DataContextException(String message) {
        super(message);
    }

    public DataContextException(String message, Exception ex) {
        super(message, ex);
    }
}

