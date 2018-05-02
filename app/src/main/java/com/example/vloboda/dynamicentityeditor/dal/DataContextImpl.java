package com.example.vloboda.dynamicentityeditor.dal;

import android.content.ContentValues;
import android.database.Cursor;
//import org.sqlite.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.dal.DataContentValues;
import com.example.dal.DataContext;
import com.example.dal.DataContextException;
import com.example.dal.DataCursor;

import java.io.IOException;
import java.util.HashMap;

public class DataContextImpl implements DataContext {
    private final SQLiteDatabase sqliteDatabase;
    private boolean hasOpendTransaction;

    public DataContextImpl(SQLiteDatabase sqliteDatabase) {
        this.sqliteDatabase = sqliteDatabase;
    }

    @Override
    public DataCursor executeCursor(String sql, Object... arguments) {
        String[] stringArgs = convertToStringsArguments(arguments);

        logQuery(sql, stringArgs);

        return new DataCursorImpl(sqliteDatabase.rawQuery(sql, stringArgs));
    }

    @Override
    public boolean exists(String sql, Object... arguments) throws DataContextException {
        String[] stringArgs = convertToStringsArguments(arguments);

        logQuery(sql, stringArgs);

        try (DataCursor cursor = new DataCursorImpl(sqliteDatabase.rawQuery(sql, stringArgs))) {
            return cursor.moveToNext();
        } catch (IOException e) {
            throw new DataContextException(e);
        }
    }

    @Override
    public Integer executeInt(String sql, Object... arguments) throws DataContextException {
        String[] stringArgs = convertToStringsArguments(arguments);

        logQuery(sql, stringArgs);

        try (DataCursor cursor = new DataCursorImpl(sqliteDatabase.rawQuery(sql, stringArgs))) {
            if (cursor.moveToNext()) {
                return cursor.getInt(0);
            }

            return null;
        } catch (IOException e) {
            throw new DataContextException(e);
        }
    }

    @Override
    public Long executeLong(String sql, Object... arguments) throws DataContextException {
        String[] stringArgs = convertToStringsArguments(arguments);

        logQuery(sql, stringArgs);

        try (DataCursor cursor = new DataCursorImpl(sqliteDatabase.rawQuery(sql, stringArgs))) {
            if (cursor.moveToNext()) {
                return cursor.getLong(0);
            }

            return null;
        } catch (IOException e) {
            throw new DataContextException(e);
        }
    }

    @Override
    public String executeString(String sql, Object... arguments) throws DataContextException {
        String[] stringArgs = convertToStringsArguments(arguments);

        logQuery(sql, stringArgs);

        try (DataCursor cursor = new DataCursorImpl(sqliteDatabase.rawQuery(sql, stringArgs))) {
            if (cursor.moveToNext()) {
                return cursor.getString(0);
            }

            return null;
        } catch (IOException e) {
            throw new DataContextException(e);
        }
    }

    @Override
    public byte[] executeBlob(String sql, Object... arguments) throws DataContextException {
        String[] stringArgs = convertToStringsArguments(arguments);

        logQuery(sql, stringArgs);

        try (DataCursor cursor = new DataCursorImpl(sqliteDatabase.rawQuery(sql, stringArgs))) {
            if (cursor.moveToNext()) {
                return cursor.getBlob(0);
            }

            return null;
        } catch (IOException e) {
            throw new DataContextException(e);
        }
    }

    private String[] convertToStringsArguments(Object[] arguments) {
        String[] stringArgs = arguments == null ? null : new String[arguments.length];

        if(stringArgs != null) {
            for(int i = 0; i < stringArgs.length; i++) {
                stringArgs[i] = String.valueOf(arguments[i]);
            }
        }

        return stringArgs;
    }

    @Override
    public void executeSql(String sql) {
        logQuery(sql, null);
        sqliteDatabase.execSQL(sql);
    }

    @Override
    public void executeSql(String sql, String[] parameters) {
        logQuery(sql, parameters);

        sqliteDatabase.execSQL(sql, parameters);
    }

    @Override
    public int delete(String table, String whereClause, Object... whereArgs) {
        String[] stringArgs = convertToStringsArguments(whereArgs);

        logQuery("DELETE FROM " + table + " " + whereClause, stringArgs);

        return sqliteDatabase.delete(table, whereClause, stringArgs);
    }

    @Override
    public int update(String table, DataContentValues values, String whereClause, Object... whereArgs) {
        String[] stringArgs = convertToStringsArguments(whereArgs);
        ContentValues parameters = getContentValues(values);

        logQuery("UPDATE " + table, stringArgs);

        return sqliteDatabase.update(table, parameters, whereClause, stringArgs);
    }

    @Nullable
    private ContentValues getContentValues(DataContentValues values) {
        ContentValues parameters = null;
        if(values != null) {
            parameters = new ContentValues(values.size());
            HashMap<String, Object> innerValues = values.getValues();
            for (String key: innerValues.keySet()) {
                Object innerValue = innerValues.get(key);

                if(innerValue == null) {
                    parameters.putNull(key);
                    continue;
                }

                if(innerValue.getClass() == String.class) {
                    parameters.put(key, (String)innerValue);
                    continue;
                }
                if(innerValue.getClass() == Integer.class) {
                    parameters.put(key, (Integer)innerValue);
                    continue;
                }
                if(innerValue.getClass() == Byte.class) {
                    parameters.put(key, (Byte)innerValue);
                    continue;
                }
                if(innerValue.getClass() == Short.class) {
                    parameters.put(key, (Short)innerValue);
                    continue;
                }
                if(innerValue.getClass() == Long.class) {
                    parameters.put(key, (Long)innerValue);
                    continue;
                }
                if(innerValue.getClass() == Float.class) {
                    parameters.put(key, (Float)innerValue);
                    continue;
                }
                if(innerValue.getClass() == Double.class) {
                    parameters.put(key, (Double)innerValue);
                    continue;
                }
                if(innerValue.getClass() == Boolean.class) {
                    parameters.put(key, (Boolean)innerValue);
                    continue;
                }
                if(innerValue.getClass() == byte[].class) {
                    parameters.put(key, (byte[])innerValue);
                    continue;
                }
                if(innerValue.getClass() == byte[].class) {
                    parameters.put(key, (byte[])innerValue);
                    continue;
                }

                throw new IllegalArgumentException("Unknown parameter type " + key);
            }
        }
        return parameters;
    }

    public long insert(String table, DataContentValues values) {
        logQuery("INSERT INTO " + table, null);
        return sqliteDatabase.insertOrThrow(table, null, getContentValues(values));
    }

    public long insertOrReplace(String table, DataContentValues values)
    {
        return sqliteDatabase.insertWithOnConflict(table, null, getContentValues(values), 5);
    }

    public void beginTransaction() {
        hasOpendTransaction = true;
        sqliteDatabase.beginTransaction();
    }

    public void commitTransaction() {
        hasOpendTransaction = false;
        sqliteDatabase.setTransactionSuccessful();
        sqliteDatabase.endTransaction();
    }

    @Override
    public void close() throws IOException {
        if(hasOpendTransaction) {
            sqliteDatabase.endTransaction();
        }
        sqliteDatabase.close();
    }

    private void logQuery(String sql, String[] stringArgs) {
        Log.d("DC", "query = " + sql);
        if (stringArgs != null) {
            Log.d("DC", "params:");

            int index = 0;
            for(String arg: stringArgs) {
                Log.d("DC", "       p" + index++ + " = " + arg);
            }
        }
    }

    class DataCursorImpl implements DataCursor {
        private Cursor sqliteCursor;

        public DataCursorImpl(Cursor sqliteCursor) {
            this.sqliteCursor = sqliteCursor;
        }

        @Override
        public int getCount() {
            return sqliteCursor.getCount();
        }

        @Override
        public boolean moveToNext() {
            return sqliteCursor.moveToNext();
        }

        @Override
        public long getLong(String columnName) {
            return sqliteCursor.getLong(sqliteCursor.getColumnIndex(columnName));
        }

        @Override
        public Long getNulLong(String columnName) {
            int index = sqliteCursor.getColumnIndex(columnName);
            return sqliteCursor.isNull(index) ? null : sqliteCursor.getLong(index);
        }

        @Override
        public String getString(String columnName) {
            int index = sqliteCursor.getColumnIndex(columnName);
            return sqliteCursor.isNull(index) ? null : sqliteCursor.getString(index);
        }

        @Override
        public byte[] getBlob(String columnName) {
            return sqliteCursor.getBlob(sqliteCursor.getColumnIndex(columnName));
        }

        @Override
        public int getInt(String columnName) {
            return sqliteCursor.getInt(sqliteCursor.getColumnIndex(columnName));
        }

        @Override
        public int getInt(int columnIndex) {
            return sqliteCursor.getInt(columnIndex);
        }

        @Override
        public boolean isNull(String columnName) {
            int columnIndex = sqliteCursor.getColumnIndex(columnName);
            if (columnIndex < 0) {
                return true;
            }
            return sqliteCursor.isNull(sqliteCursor.getColumnIndex(columnName));
        }

        @Override
        public boolean getBoolean(String columnName) {
            return getInt(columnName) == 1;
        }

        @Override
        public Boolean getNulBoolean(String columnName) {
            int index = sqliteCursor.getColumnIndex(columnName);
            return sqliteCursor.isNull(index) ? null : getInt(columnName) == 1;
        }

        @Override
        public Long getLong(int columnIndex) {
            return sqliteCursor.getLong(columnIndex);
        }

        @Override
        public String getString(int columnIndex) {
            return sqliteCursor.getString(columnIndex);
        }

        @Override
        public byte[] getBlob(int columnIndex) {
            return sqliteCursor.getBlob(columnIndex);
        }

        @Override
        public void close() throws IOException {
            sqliteCursor.close();
        }
    }
}

