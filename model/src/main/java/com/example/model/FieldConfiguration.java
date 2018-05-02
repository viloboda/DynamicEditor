package com.example.model;

public class FieldConfiguration {
    public static final int CONTROL_TYPE_TEXT = 1;
    public static final int CONTROL_TYPE_BOOLEAN = 2;
    public static final int CONTROL_TYPE_RADIO_LIST = 3;
    public static final int CONTROL_TYPE_NUMBER = 4;

    public String attributeId;
    public String hint;
    public String caption;
    public int controlType;

    public FieldConfiguration() {
    }

    public FieldConfiguration(String attributeId, String hint, String caption, int controlType) {
        this.attributeId = attributeId;
        this.hint = hint;
        this.caption = caption;
        this.controlType = controlType;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldConfiguration ac = (FieldConfiguration) o;
        return attributeId != null ? attributeId.equals(ac.attributeId) : ac.attributeId == null;

    }

    @Override
    public int hashCode() {
        return attributeId != null ? attributeId.hashCode() : 0;
    }
}