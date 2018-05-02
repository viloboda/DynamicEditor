package com.example.dal;

import com.example.model.AttributeValue;
import com.example.model.CommonDtoBase;
import com.example.model.FieldSetting;
import com.example.model.FirmDto;
import com.example.model.ObjectType;
import com.example.model.TemplateDto;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import kotlin.jvm.Throws;

public interface Repository {

    List<FirmDto> getFirms(String filter) throws DataContextException;

    void saveObject(CommonDtoBase dto);

    <T extends CommonDtoBase> T undoChanges (T dto);

    <T extends CommonDtoBase> T getInitialVersion(T dto);

    void removeObject(CommonDtoBase dto);

    List<FieldSetting> getFieldSettings(ObjectType objectType) throws DataContextException;

    List<AttributeValue> getReferenceItems(String referenceCode) throws DataContextException;

    <T extends TemplateDto> List<T> getTemplatesByType(TemplateDto.TemplateType type, Class<T> classOfT) throws DataContextException;

    long getNextObjectId();
}

