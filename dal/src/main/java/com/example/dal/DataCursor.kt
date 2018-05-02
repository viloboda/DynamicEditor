package com.example.dal

import java.io.Closeable

interface DataCursor : Closeable {

    val count: Int

    fun moveToNext(): Boolean

    fun getLong(columnName: String): Long

    fun getNulLong(columnName: String): Long?

    fun getString(columnName: String): String?

    fun getBlob(columnName: String): ByteArray?

    fun getInt(columnName: String): Int

    fun getInt(columnIndex: Int): Int

    fun isNull(columnName: String): Boolean

    fun getBoolean(columnName: String): Boolean

    fun getNulBoolean(columnName: String): Boolean?

    fun getLong(columnIndex: Int): Long?

    fun getString(columnIndex: Int): String

    fun getBlob(columnIndex: Int): ByteArray
}
