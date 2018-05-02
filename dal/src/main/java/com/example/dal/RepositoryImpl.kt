package com.example.dal

import com.example.model.*
import com.example.model.serialization.JsonSerializer
import java.util.ArrayList

class RepositoryImpl(private val dataContextFactory: DataContextFactory,
                     private val serializer: JsonSerializer) : Repository {
    override fun getNextObjectId(): Long {
        try {
            this.dataContextFactory.createReadOnly().use { dataContext ->
                return dataContext.executeLong("select max(id) + 1 from ${TableNames.OBJECT_DATA}") ?: 1
            }
        } catch (e: Exception) {
            throw DataContextException(e)
        }
    }

    override fun <T : TemplateDto?> getTemplatesByType(type: TemplateDto.TemplateType, classOfT: Class<T>): MutableList<T> {
        try {
            this.dataContextFactory.createReadOnly().use { dataContext ->
                dataContext.executeCursor(
                        "select id, name, json from ${TableNames.TEMPLATES} where type = ?",
                        type.id).use { cursor ->

                    val result = ArrayList<T>(cursor.count)

                    while (cursor.moveToNext()) {
                        val templateDto = serializer.fromJson<T>(cursor.getString("json"), classOfT)
                        templateDto!!.Id = cursor.getInt("id")
                        templateDto.Name = cursor.getString("name")

                        result.add(templateDto)
                    }

                    return result

                }
            }
        } catch (e: Exception) {
            throw DataContextException(e)
        }
    }

    override fun getReferenceItems(referenceCode: String?): MutableList<AttributeValue> {
        try {
            this.dataContextFactory.createReadOnly().use { dataContext ->
                ReferenceItemCursor(dataContext.executeCursor(
                        "SELECT code,  name FROM ${TableNames.REFERENCE_ITEMS} WHERE ref_code = ? ORDER BY sortpos",
                        referenceCode)).use { cursor ->

                    val referenceItems = ArrayList<AttributeValue>(cursor.count)

                    referenceItems += cursor

                    return referenceItems

                }
            }
        } catch (e: Exception) {
            throw DataContextException(e)
        }
    }

    override fun getFieldSettings(objectType: ObjectType): MutableList<FieldSetting> {
        val query = ("SELECT * FROM ${TableNames.FIELD_SETTINGS} " +
                "WHERE object_type = ? ORDER BY sortpos")

        try {
            this.dataContextFactory.createReadOnly().use { dataContext ->
                FieldSettingCursor(dataContext.executeCursor(query,
                        objectType.id)).use { cursor ->

                    val settings = ArrayList<FieldSetting>(cursor.count)

                    settings += cursor

                    return settings

                }
            }
        } catch (e: Exception) {
            throw DataContextException(e)
        }
    }

    override fun saveObject(dto: CommonDtoBase) {
        val initialVersion = getInitialVersion(dto.id)

        if (initialVersion != null) {
            val initialDto = serializer.fromJson(initialVersion.Attributes, dto.javaClass)
            initialDto.id = dto.id
            initialDto.objectType = ObjectType.getObjectTypes()[initialVersion.ObjectType]

            createObject(initialDto, true)
            updateGeoObject(dto, initialDto)
        } else {
            createObject(dto, false)
        }
    }

    @Throws(DataContextException::class)
    private fun createObject(dto: CommonDtoBase, saveHistory: Boolean) {
        val geoValues = convert(dto)

        try {
            this.dataContextFactory.create().use { dataContext ->
                dataContext.beginTransaction()

                if (saveHistory) {
                    val exists = dataContext.exists("SELECT 1 FROM ${TableNames.OBJECT_DATA_HISTORY} WHERE id = ? and version = 1", dto.id)

                    if (exists) {
                        // пока сохраняем только исходое состоятие, если оно уже сохранено - ничего не делаем
                        return
                    }

                    geoValues.put("version", 1)
                    dataContext.insert(TableNames.OBJECT_DATA_HISTORY, geoValues)
                } else {

                    dataContext.insert(TableNames.OBJECT_DATA, geoValues)

                    updateSearchIndex(dto, dataContext)
                }

                dataContext.commitTransaction()
            }
        } catch (e: Exception) {
            throw DataContextException("Error while saving house", e)
        }
    }

    @Throws(DataContextException::class)
    private fun updateGeoObject(dto: CommonDtoBase, dataContext: DataContext) {
        val values = convert(dto)
        dataContext.update(TableNames.OBJECT_DATA, values, "id = ?", dto.id)
    }

    @Throws(DataContextException::class)
    private fun updateGeoObject(dto: CommonDtoBase, initialVersion: CommonDtoBase) {
        try {
            this.dataContextFactory.create().use { dataContext ->
                dataContext.beginTransaction()

                updateGeoObject(dto, dataContext)
                removeSearchIndex(initialVersion, dataContext)
                updateSearchIndex(dto, dataContext)

                if (dto.state == EntityState.STATE_UNCHANGED) {
                    dataContext.delete(TableNames.OBJECT_DATA_HISTORY, "id = ?", dto.id)
                }

                dataContext.commitTransaction()
            }
        } catch (e: Exception) {
            throw DataContextException("Error while saving house", e)
        }

    }

    private fun convert(dto: CommonDtoBase): DataContentValues {
        val values = DataContentValues()

        values.put("id", dto.id)
        values.put("type", dto.objectType.id)
        values.put("attributes", serializer.toJson(dto))

        return values
    }

    private fun updateSearchIndex(dto: CommonDtoBase, dataContext: DataContext) {
        val searchValues = DataContentValues()
        searchValues.put("name", dto.name)
        searchValues.put("rowid", dto.id)
        dataContext.insert(TableNames.SEARCH_DATA, searchValues)
    }

    private fun removeSearchIndex(dto: CommonDtoBase, dataContext: DataContext) {
        val searchValues = DataContentValues()
        searchValues.put(TableNames.SEARCH_DATA, "delete")
        searchValues.put("name", dto.name)
        searchValues.put("rowid", dto.id)
        dataContext.insert(TableNames.SEARCH_DATA, searchValues)
    }

    @Throws(DataContextException::class)
    override fun <T : CommonDtoBase> undoChanges(geo: T): T? {
        try {
            this.dataContextFactory.createReadOnly().use { dataContext ->
                dataContext.beginTransaction()

                var dto: T? = null
                val initialGeoObject = getObjectData(geo.id, TableNames.OBJECT_DATA_HISTORY, dataContext)
                if (initialGeoObject != null) {
                    dto = toCommonDto(initialGeoObject, geo)
                    updateGeoObject(dto!!, dataContext)

                    dataContext.delete(TableNames.OBJECT_DATA_HISTORY, "id = ? AND version = 1", geo.id)
                }

                dataContext.commitTransaction()

                return dto
            }
        } catch (e: Exception) {
            throw DataContextException(e)
        }
    }

    override fun removeObject(dto: CommonDtoBase) {
        try {
            this.dataContextFactory.createReadOnly().use { dataContext ->
                dataContext.beginTransaction()

                dataContext.delete(TableNames.OBJECT_DATA, "id = ?", dto.id)
                dataContext.delete(TableNames.OBJECT_DATA_HISTORY, "id = ? AND version = 1", dto.id)
                removeSearchIndex(dto, dataContext)

                dataContext.commitTransaction()
            }
        } catch (e: Exception) {
            throw DataContextException(e)
        }
    }

    private fun <T : CommonDtoBase> toCommonDto(initialVersion: ObjectData, dto: T): T? {
        val initialDto = serializer.fromJson(initialVersion.Attributes, dto.javaClass)
        initialDto.id = initialVersion.Id
        initialDto.objectType = ObjectType.getObjectTypes()[initialVersion.ObjectType]
        return initialDto
    }

    @Throws(DataContextException::class)
    override fun getFirms(filter: String?): List<FirmDto> {
        try {
            this.dataContextFactory.createReadOnly().use { dataContext ->

                var query  = "SELECT * FROM ${TableNames.OBJECT_DATA} WHERE type = ?"

                if (!filter.isNullOrEmpty()) {
                    query += " AND id IN (SELECT rowid FROM ${TableNames.SEARCH_DATA} " +
                            "WHERE ${TableNames.SEARCH_DATA} MATCH '${filter.prepareForSearch()}')"
                }

                FirmDataCursor(dataContext.executeCursor(query, ObjectType.Firm.id), serializer).use { cursor ->
                    val result = ArrayList<FirmDto>(cursor.count)
                    result += cursor

                    return result
                }

            }
        } catch (e: Exception) {
            throw DataContextException(e)
        }
    }

    override fun <T : CommonDtoBase> getInitialVersion(dto: T): T? {
        val objectData = getInitialVersion(dto.id) ?: return null
        return toCommonDto(objectData, dto)
    }

    @Throws(DataContextException::class)
    private fun getInitialVersion(id: Long): ObjectData? {
        try {
            this.dataContextFactory.createReadOnly().use { dataContext ->

                var geo = getObjectData(id, TableNames.OBJECT_DATA_HISTORY, dataContext)

                if (geo == null) {
                    geo = getObjectData(id, TableNames.OBJECT_DATA, dataContext)
                }

                return geo

            }
        } catch (e: Exception) {
            throw DataContextException(e)
        }
    }

    private fun getObjectData(id: Long, table: String, dataContext: DataContext): ObjectData? {
        try {
            ObjectDataCursor(dataContext.executeCursor(
                    "SELECT * FROM $table gd WHERE gd.id = ?", id)).use { cursor ->
                for (g in cursor) {
                    return g
                }
            }
        } catch (e: Exception) {
            throw DataContextException(e)
        }

        return null
    }

    private fun String?.prepareForSearch(startWith: Boolean = true): String {
        var source: String = this ?: return ""

        source = source.trimEnd()

        val result = StringBuilder(source.length)

        for (i in 0 until source.length) {
            val c = source[i]

            if (Character.isAlphabetic(c.toInt()) || Character.isDigit(c)) {
                result.append(c)
            } else {
                result.append(" ")
            }
        }

        source = result.toString()
        return if (source.trim { it <= ' ' }.isNullOrEmpty()) {
            source
        } else source + if (startWith) "*" else ""

    }

    private inner class FieldSettingCursor internal constructor(cursor: DataCursor) : DataCursorAbstract<FieldSetting>(cursor) {

        @Throws(DataContextException::class)
        override fun createObject(cursor: DataCursor): FieldSetting {
            val id = cursor.getLong("id")
            val type = cursor.getInt("type")
            val name = cursor.getString("name")
            val parentName = cursor.getString("parent_name")
            val fieldCode = cursor.getString("field_code")
            val referenceCode = cursor.getString("reference_code")

            var parentId: Long? = null
            if (!cursor.isNull("parent_id")) {
                parentId = cursor.getLong("parent_id")
            }

            return FieldSetting(id, type, name, parentName, parentId, fieldCode, referenceCode)
        }
    }

    private inner class ReferenceItemCursor internal constructor(cursor: DataCursor) : DataCursorAbstract<AttributeValue>(cursor) {

        @Throws(DataContextException::class)
        override fun createObject(cursor: DataCursor): AttributeValue {
            val code = cursor.getLong("code")
            val caption = cursor.getString("name")

            return AttributeValue(code, caption)
        }
    }
}
