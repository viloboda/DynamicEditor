package com.example.dal.migration;

import com.example.dal.DataContext;
import com.example.dal.DataContextException;
import com.example.dal.MigrationException;

public class MigrationsDatabaseHelper {

	private int dbVersion;
	private Migrator migrator;

	public MigrationsDatabaseHelper(int dbVersion, String packageName) {
		this.dbVersion = dbVersion;
		this.migrator = new Migrator(packageName);
	}

	public void migrate(DataContext dataContext) throws MigrationException {
		try {

			int currentVersion = 0;
			if(!dataContext.exists("SELECT 1 FROM sqlite_master WHERE type='table' AND name='version';")) {
				dataContext.executeSql("CREATE TABLE version (id INTEGER PRIMARY KEY)");
			}

			currentVersion = dataContext.executeInt("SELECT max(id) as id FROM version");

			migrator.upgrade(dataContext, currentVersion, dbVersion);
		} catch (DataContextException e) {
			e.printStackTrace();
		}
	}
}