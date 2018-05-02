package com.example.model;

import java.util.List;

public class AttachmentDto {
    public long Id;

    public long ObjectId;

    public AttachmentAttributesDto Attributes;

    private int state = EntityState.STATE_UNCHANGED;

    public long getId() {
        return Id;
    }

    public ObjectType getObjectType() {
        return ObjectType.Attachment;
    }

    public Long getParentId() {
        return ObjectId;
    }

    public int getState() {
        return this.state;
    }

    public transient List<TagInfo> TagInfos;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AttachmentDto that = (AttachmentDto) o;

        if (Id != that.Id) return false;
        return Attributes != null ? Attributes.equals(that.Attributes) : that.Attributes == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (Id ^ (Id >>> 32));
        result = 31 * result + (Attributes != null ? Attributes.hashCode() : 0);
        return result;
    }
}

