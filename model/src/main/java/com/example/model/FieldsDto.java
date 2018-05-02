package com.example.model;

import java.util.Collection;
import java.util.Set;

public interface FieldsDto {
    FieldDto getField(String fieldCode);

    boolean IsInAddingMode();

    void setFields(Collection<FieldDto> attributes, Set<String> removedAttributes);

    void clearRemoveFields();

    Collection<FieldDto> getFields();

    void addRemovedField(FieldDto field);

    void confirmRemoveFields();
}

