package com.example.dal;

public interface DataContextFactory {
    /*
    Readonly context
     */
    DataContext createReadOnly();
    /*
    Read-write context
     */
    DataContext create();
}
