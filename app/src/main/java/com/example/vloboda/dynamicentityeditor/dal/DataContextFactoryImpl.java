package com.example.vloboda.dynamicentityeditor.dal;

import android.os.Environment;

import com.example.dal.DataContext;
import com.example.dal.DataContextFactory;
import com.example.dal.MigrationException;
import com.example.dal.migration.MigrationsDatabaseHelper;
import com.example.vloboda.dynamicentityeditor.AppState;

import org.sqlite.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.IOException;

public class DataContextFactoryImpl implements DataContextFactory {

    private int USER_DB_VERSION = 1;

    private final MigrationsDatabaseHelper migrationHelper = new MigrationsDatabaseHelper(USER_DB_VERSION, "com.example.dal.migration.migrations");
    private boolean alreadyMigrated = false;

    @Override
    public DataContext createReadOnly() {
        return createInternal(true);
    }

    @Override
    public DataContext create() {
        return createInternal(false);
    }

    private File getDatabasePath() {
        return new File(AppState.AppDirectory, "database.sqlite");
    }
    private DataContext createInternal(boolean readOnly) {

        DataContextImpl dataContext = null;

        try {
            dataContext = new DataContextImpl(getOrCreateUserDatabase(readOnly));

            migrateDatabase(dataContext);
        } catch (Exception e)
        {
            if(dataContext != null) {
                try {
                    dataContext.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            throw e;
        }

        return dataContext;
    }

    private void migrateDatabase(DataContextImpl dataContext) {
        if(!alreadyMigrated) {
            try {
                migrationHelper.migrate(dataContext);
            } catch (MigrationException e) {
                throw new RuntimeException(e);
            }
            alreadyMigrated = true;
        }
    }

    public boolean dataBaseExists() {
        return getDatabasePath().exists();
    }

    private SQLiteDatabase getOrCreateUserDatabase(boolean readOnly) {
        File dbFile = getDatabasePath();

        int flags = readOnly ? SQLiteDatabase.OPEN_READONLY : SQLiteDatabase.OPEN_READWRITE;
        SQLiteDatabase udb = SQLiteDatabase.openDatabase(dbFile.getAbsolutePath(), null, flags | SQLiteDatabase.CREATE_IF_NECESSARY);

        return udb;
    }
}

