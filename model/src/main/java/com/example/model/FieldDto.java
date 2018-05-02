package com.example.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class FieldDto {
    @SerializedName("code")
    private String code; // Код атрибута

    @SerializedName("value")
    private String value; // Значение атрибута строкой

    @SerializedName("r_values")
    private List<Long> referenceValues; // Значение атрибута через ссылки на справочник

    @SerializedName("change_info")
    private ChangeInfoDto changeInfo; // Информация об изменении (где и когда)

    // EntityState
    @SerializedName("state")
    public int State; // Статус (не измененный, новый, измененный, удаленный)

    public FieldDto(String code) {
        this.code = code;
    }

    public FieldDto(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public FieldDto(String code, List<Long> values) {
        this.code = code;
        this.referenceValues = values;
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public static String getStringValue(FieldDto dto) {
        if (dto == null) {
            return null;
        }

        return StringHelperKt.isNullOrEmpty(dto.value) ? null : dto.value;
    }

    public static boolean getBoolValue(FieldDto dto) {
        return dto != null && !StringHelperKt.isNullOrEmpty(dto.value) && dto.value.equals("True");

    }

    public static int getIntValue(FieldDto dto) {
        if (dto == null) {
            return 0;
        }

        return StringHelperKt.isNullOrEmpty(dto.value) ? 0 : Integer.valueOf(dto.value);
    }

    static Long getLongValue(FieldDto dto) {
        List<Long> result = dto != null ? dto.getReferenceValues() : null;
        if (result == null || result.isEmpty()) {
            return null;
        }

        return result.get(0);
    }

    public List<Long> getReferenceValues() {
        return this.referenceValues;
    }

    public void setChangeInfo(ChangeInfoDto changeInfo) {
        this.changeInfo = changeInfo;
    }

    public ChangeInfoDto getChangeInfo() {
        return this.changeInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof FieldDto)) return false;
        FieldDto other = (FieldDto) o;
        return this.code.equals(other.code);
    }

    @Override
    public int hashCode() {
        if(code == null) {
            return 0;
        }
        return code.hashCode();
    }

    @Override
    public String toString() {
        return code;
    }

    public boolean isEmpty() {
        return StringHelperKt.isNullOrEmpty(this.value)
                && (this.referenceValues == null || this.referenceValues.isEmpty());
    }

    public FieldDto copy() {
        FieldDto result = new FieldDto(this.code);
        result.value = this.value;
        result.State = this.State;
        result.changeInfo = this.changeInfo;
        if (this.referenceValues != null) {

            result.referenceValues = new ArrayList<>(this.referenceValues);
        }

        return result;
    }

    public void setRemovedState() {
        State = EntityState.STATE_REMOVED;
    }

    public static class FieldDtoByValueEqualityComparer implements CollectionHelper.EqualityComparer<FieldDto>
    {
        @Override
        public boolean equals(FieldDto a, FieldDto b) {
            if (a == null || b == null) {
                return false;
            }

            if (!a.getCode().equals(b.getCode())) {
                return false;
            }

            if (a.getValue() != null && !a.getValue().equals(b.getValue())) {
                return false;
            }

            if (b.getValue() != null && !b.getValue().equals(a.getValue())) {
                return false;
            }

            if (a.getReferenceValues() != null && b.getReferenceValues() != null) {
                if (!CollectionHelper.equalLists(a.getReferenceValues(), b.getReferenceValues())) {
                    return false;
                }
            } else if (a.getReferenceValues() == null && b.getReferenceValues() != null) {
                return false;
            } else if (a.getReferenceValues() != null && b.getReferenceValues() == null) {
                return false;
            }

            return true;
        }
    }
}

