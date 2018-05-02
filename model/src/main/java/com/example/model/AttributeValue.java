package com.example.model;

public class AttributeValue {
    public long id;
    public String caption;
    public boolean isSelected;
    public boolean bold;
    public Object tag;

    public AttributeValue() {
    }

    public AttributeValue(long id, String caption) {
        this.id = id;
        this.caption = caption;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AttributeValue that = (AttributeValue) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
