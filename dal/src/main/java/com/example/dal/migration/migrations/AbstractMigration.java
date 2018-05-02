package com.example.dal.migration.migrations;

import com.example.dal.DataContext;
import com.example.dal.DataContextException;

public abstract class AbstractMigration {
    // Эта версия нужна для пересоздания базы
    // Если текущая версия базы отличается от этого - база пересоздается.
    public static final int CUSTOM_USER_VERSION = 5;

    protected DataContext dataContext;

    public AbstractMigration(){}

    public void setDatabase(DataContext dataContext){
        this.dataContext = dataContext;
    }

    public abstract void up() throws DataContextException;

    public abstract void down();
}
