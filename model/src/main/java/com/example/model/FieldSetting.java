package com.example.model;

public class FieldSetting {
    public static final int TYPE_TEXT = 1;
    // список с единичным выбором
    public static final int TYPE_BOOL_SINGLE = 2;
    //список с множественным выбором
    public static final int TYPE_BOOL_MULTY = 3;
    // True или False
    public static final int TYPE_BOOL_SIMPLE = 13;
    public static final int TYPE_INT = 4;

    public static String FIELD_NAME = "name";
    public static String FIELD_ADDRESS_NAME = "address_name";

    public static String FIELD_LEGAL_NAME = "LegalName";
    public static String FIELD_LEGAL_FORM = "OrganizationLegalForm";

    public static String FIELD_START_GROUP = "start_group";
    public static String FIELD_END_GROUP = "end_group";

    private final long id;
    private final int type;
    private final String name;
    private final Long parentId;
    private final String parentName;
    private final String fieldCode;
    private final String referenceCode;

    public FieldSetting(long id, int type, String name, String parent_name, Long parentId,
                        String fieldCode, String referenceCode) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.parentName = parent_name;
        this.parentId = parentId;
        this.fieldCode = fieldCode;
        this.referenceCode = referenceCode;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Long getParentId() {
        return parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public String getFieldCode() {
        return fieldCode;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public long getId() {
        return id;
    }
}
