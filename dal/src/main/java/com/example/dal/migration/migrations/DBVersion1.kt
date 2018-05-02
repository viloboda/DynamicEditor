package com.example.dal.migration.migrations

import com.example.dal.DataContentValues
import com.example.model.*
import com.example.model.serialization.GsonSerializer

class DBVersion1 : AbstractMigration() {

    override fun up() {
        dataContext.executeSql("CREATE TABLE ${TableNames.METADATA} (custom_version INTEGER NOT NULL)")
        val metadata = DataContentValues(1)
        metadata.put("custom_version", AbstractMigration.CUSTOM_USER_VERSION)
        dataContext.insert(TableNames.METADATA, metadata)

        dataContext.executeSql("CREATE TABLE ${TableNames.OBJECT_DATA} " +
                "( id INTEGER NOT NULL, type INTEGER NOT NULL, attributes TEXT NULL, PRIMARY KEY ( id ) )")
        dataContext.executeSql("CREATE INDEX idx_${TableNames.OBJECT_DATA}_type ON ${TableNames.OBJECT_DATA}(type)")

        dataContext.executeSql("CREATE TABLE ${TableNames.OBJECT_DATA_HISTORY} " +
                "( id INTEGER NOT NULL, version INTEGER NOT NULL, type INTEGER NOT NULL, attributes TEXT NULL, PRIMARY KEY ( id, version ) )")
        dataContext.executeSql("CREATE INDEX idx_${TableNames.OBJECT_DATA_HISTORY}_type ON ${TableNames.OBJECT_DATA}(type)")

        dataContext.executeSql("CREATE TABLE ${TableNames.REFERENCE_ITEMS} " +
                "( id INTEGER NOT NULL, ref_code TEXT NOT NULL, code INTEGER NOT NULL, name TEXT NULL, sortpos INTEGER, PRIMARY KEY ( id ) )")

        dataContext.executeSql("CREATE TABLE ${TableNames.FIELD_SETTINGS} " +
                "( id INTEGER NOT NULL, object_type INTEGER NOT NULL, type INTEGER NOT NULL, " +
                "name TEXT, parent_id INTEGER, parent_name TEXT, field_code TEXT, reference_code TEXT, " +
                "sortpos INTEGER NOT NULL, PRIMARY KEY ( id ) )")

        dataContext.executeSql("CREATE VIRTUAL TABLE ${TableNames.SEARCH_DATA} USING fts5(content=\"\", name)")

        dataContext.executeSql("CREATE TABLE ${TableNames.TEMPLATES} " +
                "( id INTEGER NOT NULL, type INTEGER NOT NULL, name TEXT NOT NULL, json TEXT NOT NULL, PRIMARY KEY ( id, type ) )")

        val serializer = GsonSerializer()

        val firm1 = FirmDto(1)
        firm1.Fields = hashSetOf()
        firm1.Fields.add(FieldDto(FieldSetting.ATTRIBUTE_NAME, "2ГИС, городской информационный справочник"))
        firm1.Fields.add(FieldDto(FieldSetting.ATTRIBUTE_ADDRESS_NAME, "пл. Карла Маркса 7"))
        firm1.Fields.add(FieldDto(FieldSetting.ATTRIBUTE_LEGAL_NAME, "ДубльГИС"))
        firm1.Fields.add(FieldDto(FieldSetting.ATTRIBUTE_LEGAL_FORM, mutableListOf(1000)))
        firm1.Fields.add(FieldDto("Floor", "14"))

        val firm2 = FirmDto(2)
        firm2.Fields = hashSetOf()
        firm2.Fields.add(FieldDto(FieldSetting.ATTRIBUTE_NAME, "Додо Пицца, федеральная сеть пицерий"))
        firm2.Fields.add(FieldDto(FieldSetting.ATTRIBUTE_ADDRESS_NAME, "Красный проспект 17"))
        firm2.Fields.add(FieldDto(FieldSetting.ATTRIBUTE_LEGAL_NAME, "Ромашка"))
        firm2.Fields.add(FieldDto(FieldSetting.ATTRIBUTE_LEGAL_FORM, mutableListOf(1001)))

        val firm3 = FirmDto(3)
        firm3.Fields = hashSetOf()
        firm3.Fields.add(FieldDto(FieldSetting.ATTRIBUTE_NAME, "Прайм-сервис, специализированный сервис по ремонту"))
        firm3.Fields.add(FieldDto(FieldSetting.ATTRIBUTE_ADDRESS_NAME, "Энергетиков проезд, 8 / 3"))
        firm3.Fields.add(FieldDto(FieldSetting.ATTRIBUTE_LEGAL_NAME, "ИП Васильев"))
        firm3.Fields.add(FieldDto(FieldSetting.ATTRIBUTE_LEGAL_FORM, mutableListOf(1003)))

        saveObject(firm1, serializer)
        saveObject(firm2, serializer)
        saveObject(firm3, serializer)

        saveRefItem(1, "legal_form", 1, "ООО", 1)
        saveRefItem(2, "legal_form", 2, "ЗАО", 2)
        saveRefItem(3, "legal_form", 3, "ОАО", 3)
        saveRefItem(4, "legal_form", 4, "ИП", 4)
        saveRefItem(5, "legal_form", 5, "МДОУ", 5)
        saveRefItem(6, "legal_form", 6, "МАО", 6)

        saveFieldSetting(1, ObjectType.Firm.id, FieldSetting.TYPE_TEXT, "Название", FieldSetting.ATTRIBUTE_NAME, null, 1)
        saveFieldSetting(2, ObjectType.Firm.id, FieldSetting.TYPE_TEXT, "Адрес", FieldSetting.ATTRIBUTE_ADDRESS_NAME, null, 2)
        saveFieldSetting(3, ObjectType.Firm.id, FieldSetting.TYPE_TEXT, "Юр. название", FieldSetting.ATTRIBUTE_LEGAL_NAME, null, 3)
        saveFieldSetting(4, ObjectType.Firm.id, FieldSetting.TYPE_BOOL_SINGLE, "ОПФ", FieldSetting.ATTRIBUTE_LEGAL_FORM, "legal_form", 4)
        saveFieldSetting(5, ObjectType.Firm.id, FieldSetting.TYPE_BOOL_SIMPLE, "Wi-Fi", "WiFi", null, 5)
        saveFieldSetting(6, ObjectType.Firm.id, FieldSetting.TYPE_INT, "Этаж", "Floor", null, 6)
    }

    private fun saveObject(objectDto: CommonDtoBase, serializer: GsonSerializer) {
        val parameters = DataContentValues()
        parameters.put("id", objectDto.id)
        parameters.put("type", objectDto.objectType.id)
        parameters.put("attributes", objectDto.toJson(serializer))
        dataContext.insert(TableNames.OBJECT_DATA, parameters)

        val searchValues = DataContentValues()
        searchValues.put("name", objectDto.name)
        searchValues.put("rowid", objectDto.id)
        dataContext.insert(TableNames.SEARCH_DATA, searchValues)
    }

    private fun saveRefItem(id: Int, refCode: String, code: Int, name: String, sortpos: Int) {
        val parameters = DataContentValues()
        parameters.put("id", id)
        parameters.put("ref_code", refCode)
        parameters.put("code", code)
        parameters.put("name", name)
        parameters.put("sortpos", sortpos)
        dataContext.insert(TableNames.REFERENCE_ITEMS, parameters)
    }

    private fun saveFieldSetting(id: Long, objectType: Int, type: Int, name: String, fieldCode: String, refCode: String?, sortpos: Int) {
        val parameters = DataContentValues()
        parameters.put("id", id)
        parameters.put("object_type", objectType)
        parameters.put("type", type)
        parameters.put("name", name)
        parameters.put("field_code", fieldCode)
        parameters.put("reference_code", refCode)
        parameters.put("sortpos", sortpos)
        dataContext.insert(TableNames.FIELD_SETTINGS, parameters)
    }

    override fun down() {

    }
}

