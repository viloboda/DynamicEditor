package com.example.model;

import com.google.gson.annotations.SerializedName;

public class AttachmentAttributesDto {
    @SerializedName("name")
    public String Name;

    @SerializedName("path")
    public String Path;

    @SerializedName("tags")
    public String Tags;

    @SerializedName("size")
    public int Size;

    @SerializedName("change_info")
    public ChangeInfoDto ChangeInfo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AttachmentAttributesDto that = (AttachmentAttributesDto) o;

        if (Name != null ? !Name.equals(that.Name) : that.Name != null) return false;
        if (Path != null ? !Path.equals(that.Path) : that.Path != null) return false;
        return Tags != null ? Tags.equals(that.Tags) : that.Tags == null;

    }

    @Override
    public int hashCode() {
        int result = Name != null ? Name.hashCode() : 0;
        result = 31 * result + (Path != null ? Path.hashCode() : 0);
        result = 31 * result + (Tags != null ? Tags.hashCode() : 0);
        return result;
    }
}
