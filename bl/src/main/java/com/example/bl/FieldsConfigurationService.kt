package com.example.bl

import com.example.model.FieldConfiguration
import com.example.model.ObjectType

interface FieldsConfigurationService {
    fun getConfiguration(objectType: ObjectType): List<FieldConfiguration>
}
