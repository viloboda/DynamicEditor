package com.example.model;

import java.util.ArrayList;
import java.util.List;

public class ListFieldConfiguration extends FieldConfiguration {
    public ListFieldConfiguration(int controlType) {
        this.controlType = controlType;
        values = new ArrayList<>(10);
    }

    public List<AttributeValue> values;
}

