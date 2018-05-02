package com.example.model;

public class FirmDto extends CommonDtoBase {

    public FirmDto(long id) {
        super(id, ObjectType.Firm);
    }

    private FirmDto() {
    }

    @Override
    public String getName() {
        return FieldDto.getStringValue(getField(FieldSetting.FIELD_NAME));
    }

    @Override
    public String getDescription() {
        return FieldDto.getStringValue(getField(FieldSetting.FIELD_ADDRESS_NAME));
    }

    @Override
    public String getDescription2() {
        String floor = FieldDto.getStringValue(getField("Floor"));
        if (StringHelperKt.isNullOrEmpty(floor)) {
            return null;

        }
        return "Этаж " + floor;
    }

    @Override
    public ObjectType getObjectType() {
        return ObjectType.Firm;
    }

    public FirmDto copy() {
        FirmDto dtoCopy = new FirmDto();
        copyTo(dtoCopy);
        return dtoCopy;
    }
}
