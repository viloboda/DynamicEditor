package com.example.model;

import java.util.HashMap;
import java.util.Map;

public enum ObjectType {
    None(0),
    Firm(1),
    Attachment(2);

    private final int id;

    ObjectType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static Map<Integer, ObjectType> getObjectTypes() {
        Map<Integer, ObjectType> objectTypeValues = new HashMap<>(ObjectType.values().length);
        for (ObjectType gt : ObjectType.values()) {
            objectTypeValues.put(gt.getId(), gt);
        }

        return objectTypeValues;
    }
}
