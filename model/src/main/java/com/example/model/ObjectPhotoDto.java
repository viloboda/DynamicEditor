package com.example.model;

import java.util.ArrayList;
import java.util.List;

public class ObjectPhotoDto {

    private SimpleDto parentDto;
    private List<AttachmentDto> removedPhotos = new ArrayList<>();
    private List<AttachmentDto> photos = new ArrayList<>();

    public List<TagInfo> getTags() {
        return tags;
    }

    private List<TagInfo> tags = new ArrayList<>();

    public ObjectPhotoDto(SimpleDto parentDto) {
        this.parentDto = parentDto;
    }

    public void addPhoto(AttachmentDto dto) {
        this.removedPhotos.remove(dto);
        this.photos.add(dto);
    }

    public List<AttachmentDto> getPhotos() {
        return this.photos;
    }


    public void setPhotos(List<AttachmentDto> photos) {
        this.photos.clear();
        this.photos.addAll(photos);
    }

    public void removePhoto(AttachmentDto currentPhoto) {
        this.removedPhotos.add(currentPhoto);
        this.photos.remove(currentPhoto);
    }

    public boolean hasPhotos() {
        return !this.photos.isEmpty();
    }

    public long getId() {
        return this.parentDto.getId();
    }

    public ObjectType getObjectType() {
        return this.parentDto.getObjectType();
    }

    public Long getParentId() {
        return parentDto.getId();
    }

    public int getState() {
        return parentDto.getState();
    }

    public List<TagInfo> getPhotoTagsInfoCopy() {
        List<TagInfo> result = new ArrayList<>();
        for (TagInfo t: tags) {
            result.add(new TagInfo(t.getKey()));
        }

        return result;
    }

    public List<AttachmentDto> getRemovedPhotos() {
        return this.removedPhotos;
    }

    public void addTag(String name) {
        tags.add(new TagInfo(name));
    }

    public SimpleDto getParentDto() {
        return parentDto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObjectPhotoDto that = (ObjectPhotoDto) o;

        if (!CollectionHelper.equalLists(photos, that.photos)) {
            return false;
        }

        return true;

    }

    @Override
    public int hashCode() {
        return photos != null ? photos.hashCode() : 0;
    }
}

