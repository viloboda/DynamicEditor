package com.example.dal;

import java.io.IOException;
import java.util.Iterator;

public abstract class DataCursorAbstract<Type> implements Iterable<Type>, AutoCloseable {

    private DataCursor cursor;

    public DataCursorAbstract(DataCursor cursor) {
        this.cursor = cursor;
    }

    protected abstract Type createObject(DataCursor cursor) throws DataContextException;

    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public Iterator<Type> iterator() {
        Iterator<Type> it = new Iterator<Type>() {

            @Override
            public boolean hasNext() {
                return cursor.moveToNext();
            }

            @Override
            public Type next() {
                try {
                    return createObject(cursor);
                } catch (DataContextException e) {
                    throw new RuntimeException("Error while reading data", e);
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        return it;
    }

    @Override
    public void close() throws IOException {
        cursor.close();
    }
}
