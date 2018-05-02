package com.example.model;

import com.example.model.serialization.JsonSerializer;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class CommonDtoBase implements SimpleDto {

    private transient long id;
    private transient ObjectType objectType;
    private transient SimpleDto parentDto;

    @SerializedName("fields")
    public HashSet<FieldDto> Fields;

    @SerializedName("removed_fields")
    public HashSet<FieldDto> RemovedFields;

    @SerializedName("change_info")
    private ChangeInfoDto changeInfo;

    @SerializedName("state")
    public int State = EntityState.STATE_UNCHANGED;

    public transient boolean IsAddingMode;

    CommonDtoBase() {

    }

    CommonDtoBase(long id, ObjectType objectType) {
        this.id = id;
        this.objectType = objectType;
    }

    @Override
    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    public boolean isRemoved() {
        return this.State == EntityState.STATE_REMOVED;
    }

    public String toJson(JsonSerializer jsonSerializer) {
        return jsonSerializer.toJson(this);
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public Long getParentId() {
        return parentDto != null ? parentDto.getId() : null;
    }

    @Override
    public int getState() {
        return State;
    }

    private transient ObjectPhotoDto objectPhotoDto;
    @Override
    public ObjectPhotoDto getObjectPhotos(){

        if(objectPhotoDto == null) {
            objectPhotoDto = new ObjectPhotoDto(this);
            addTags(objectPhotoDto);
        }

        return objectPhotoDto;
    }

    private void addTags(ObjectPhotoDto objectPhotoDto) {
        objectPhotoDto.addTag("name");
    }

    @Override
    public abstract String getName();

    @Override
    public abstract String getDescription();

    @Override
    public ChangeInfoDto getChangeInfo() {
        return changeInfo;
    }

    @Override
    public void setChangeInfo(ChangeInfoDto changeInfo) {
        this.changeInfo = changeInfo;

        if (State == EntityState.STATE_NEW || State == EntityState.STATE_REMOVED) {
            return;
        }

        if (changeInfo != null) {
            State = EntityState.STATE_CHANGED;
        } else {
            State = EntityState.STATE_UNCHANGED;
        }

    }

    @Override
    public void setFields(Collection<FieldDto> values, Set<String> removedFields) {
        if (Fields == null) {
            Fields = new HashSet<>(values.size());
        }

        for (FieldDto value : values) {
            Fields.remove(value);
            Fields.add(value);
        }

        for(String attr: removedFields) {
            Fields.remove(new FieldDto(attr, ""));
        }
    }

    @Override
    public void clearRemoveFields() {
        if (RemovedFields != null) {
            RemovedFields.clear();
        }
    }

    @Override
    public Collection<FieldDto> getFields() {
        if (Fields == null) {
            return new ArrayList<>();
        }

        return Fields;
    }

    @Override
    public void addRemovedField(FieldDto field) {
        if (RemovedFields == null) {
            RemovedFields = new HashSet<>();
        }

        RemovedFields.add(field);
    }

    public FieldDto getField(String code) {

        if (Fields == null) {
            return null;
        }

        for (FieldDto attribute : Fields) {
            if (attribute.getCode().equals(code)) {
                return attribute;
            }
        }

        return null;
    }

    @Override
    public boolean IsInAddingMode() {
        return IsAddingMode;
    }

    public void copyTo(CommonDtoBase dto) {
        dto.id = dto.getId();
        dto.setObjectType(objectType);
        dto.Fields = Fields;
        dto.RemovedFields = RemovedFields;
        dto.State = State;
        dto.setChangeInfo(changeInfo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommonDtoBase commonDto = (CommonDtoBase) o;

        if (id != commonDto.id) return false;

        if ((State == EntityState.STATE_REMOVED || commonDto.State == EntityState.STATE_REMOVED) && State != commonDto.State)
            return false;

        if (!CollectionHelper.equalLists(Fields, commonDto.Fields, new FieldDto.FieldDtoByValueEqualityComparer())) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    String getStringValue(FieldDto dto) {
        String result = dto != null ? dto.getValue() : null;
        if (StringHelperKt.isNullOrEmpty(result)) {
            return null;
        }

        return result;
    }
}
