package com.example.model;

public abstract class TemplateDto {
    public transient int Id;
    public transient String Name;

    public TemplateDto() {
    }

    public TemplateDto(String name) {
        this.Name = name;
    }

    public abstract boolean isDefault();

    @Override
    public String toString() {
        return Name;
    }

    public enum TemplateType {
        EditCard(1),
        WorkTime(2),
        TaskResolutionNotFound(3),
        ReadonlyCard(4),
        EditHouse(5),
        EditHouseEntrance(6),
        EditOtherPoint(7),
        EditOtherLine(8),
        EditOtherPolygon(9);

        private final int id;

        TemplateType(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }
}