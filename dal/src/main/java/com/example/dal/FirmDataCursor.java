package com.example.dal;

import com.example.model.FirmDto;
import com.example.model.ObjectType;
import com.example.model.serialization.JsonSerializer;

public class FirmDataCursor extends DataCursorAbstract<FirmDto> {
    private JsonSerializer serializer;

    FirmDataCursor(DataCursor cursor, JsonSerializer serializer) {
        super(cursor);
        this.serializer = serializer;
    }

    @Override
    protected FirmDto createObject(DataCursor cursor) throws DataContextException {

        try {
            String attributes = cursor.getString("attributes");
            FirmDto result = serializer.fromJson(attributes, FirmDto.class);
            result.setId(cursor.getLong("id"));
            result.setObjectType(ObjectType.Firm);

            return result;
        } catch (Exception ex) {
            throw new DataContextException(ex);
        }

    }
}
