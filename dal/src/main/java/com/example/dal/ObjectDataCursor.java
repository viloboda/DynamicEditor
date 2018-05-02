package com.example.dal;

public class ObjectDataCursor extends DataCursorAbstract<ObjectData> {

    ObjectDataCursor(DataCursor cursor) {
        super(cursor);
    }

    @Override
    protected ObjectData createObject(DataCursor cursor) throws DataContextException {

        try {
            ObjectData result = new ObjectData();
            result.Id = cursor.getLong("id");
            result.ObjectType = cursor.getInt("type");
            result.Attributes = cursor.getString("attributes");

            return result;
        } catch (Exception ex) {
            throw new DataContextException(ex);
        }

    }
}

