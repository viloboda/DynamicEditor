package com.example.model;

import java.util.Collection;
import java.util.Set;

public interface SimpleDto {
    long getId();

    void setId(long id);

    ObjectType getObjectType();

    Long getParentId();

    int getState(); // EntityState

    ObjectPhotoDto getObjectPhotos();

    String getName();

    String getDescription();

    String getDescription2();

    boolean IsInAddingMode();

    ChangeInfoDto getChangeInfo();

    void setChangeInfo(ChangeInfoDto changeInfo);

    void clearRemoveFields();

    Collection<FieldDto> getFields();

    void addRemovedField(FieldDto field);

    FieldDto getField(String fieldCode);

    void setFields(Collection<FieldDto> fields, Set<String> removedFields);
}
