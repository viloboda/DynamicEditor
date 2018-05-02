package com.example.dal;

public interface DataContext extends AutoCloseable {
    DataCursor executeCursor(String sql, Object... arguments);
    boolean exists(String sql, Object... arguments) throws DataContextException;
    Integer executeInt(String sql, Object... arguments) throws DataContextException;
    Long executeLong(String sql, Object... arguments) throws DataContextException;
    String executeString(String sql, Object... arguments) throws DataContextException;
    byte[] executeBlob(String sql, Object... arguments) throws DataContextException;

    void executeSql(String sql);
    void executeSql(String sql, String[] parameters);
    int update(String table, DataContentValues values, String whereClause, Object... whereArgs);
    int delete(String table, String whereClause, Object... whereArgs);
    long insert(String table, DataContentValues values);
    long insertOrReplace(String table, DataContentValues values);

    void beginTransaction();
    void commitTransaction();
}

