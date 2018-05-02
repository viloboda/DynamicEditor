package com.example.dal.migration;

import com.example.dal.*;
import com.example.dal.migration.migrations.AbstractMigration;

class Migrator {
    private String packageName;

    Migrator(String packageName) {
        this.packageName = packageName;
    }

    void upgrade(DataContext db, int oldVersion, int newVersion) throws MigrationException {
        for (int x = (oldVersion + 1); x <= newVersion; x++) {
            try {
                db.beginTransaction();

                handleUp(db, "DBVersion" + x);

                DataContentValues params = new DataContentValues(1);
                params.put("id", x);
                db.insert("version", params);

                db.commitTransaction();
            } catch (Exception e) {
                throw new MigrationException(e);
            }
        }
    }

    private AbstractMigration getMigrationForPackageAndName(DataContext db, String pckName, String clzzName)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        AbstractMigration migration = (AbstractMigration) Class.forName(pckName + "." + clzzName).newInstance();
        migration.setDatabase(db);
        return migration;
    }

    private void handleUp(DataContext db, String clzzName) throws InstantiationException,
            IllegalAccessException, ClassNotFoundException, DataContextException {
        AbstractMigration migration = getMigrationForPackageAndName(db, packageName, clzzName);
        migration.up();
    }
}