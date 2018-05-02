package com.example.bl

import com.example.bl.ui.DynamicToaster
import com.example.dal.DataContextException
import com.example.dal.Repository
import com.example.model.FieldConfiguration
import com.example.model.AttributeValue
import com.example.model.FieldSetting
import com.example.model.ListFieldConfiguration
import com.example.model.ObjectType

import java.util.ArrayList
import java.util.HashMap

class FieldsConfigurationServiceImpl(private val repository: Repository, private val toaster: DynamicToaster) : FieldsConfigurationService {

    override fun getConfiguration(objectType: ObjectType): List<FieldConfiguration> {
        var configuration: List<FieldConfiguration>? = null

        try {
            val settings = repository.getFieldSettings(objectType)

            configuration = transformConfiguration(settings)

        } catch (e: DataContextException) {
            toaster.showError("Error while getting configuration", e)
        }

        if (configuration == null) {
            configuration = ArrayList()
        }

        return configuration
    }

    private fun transformConfiguration(settings: List<FieldSetting>): List<FieldConfiguration> {
        val result = ArrayList<FieldConfiguration>(settings.size)

        val listSettings = HashMap<Long, List<FieldSetting>>(5)

        for (setting in settings) {
            if (setting.type == FieldSetting.TYPE_BOOL_MULTY || setting.type == FieldSetting.TYPE_BOOL_SINGLE && setting.parentId != null) {
                var ls = listSettings[setting.parentId] as ArrayList<FieldSetting>?
                if (ls == null) {
                    ls = ArrayList(5)
                }
                ls.add(setting)
            } else {
                if (setting.referenceCode != null) {
                    val listAttr = ListFieldConfiguration(getControlType(setting.type))
                    listAttr.caption = setting.name
                    listAttr.fieldCode = setting.fieldCode
                    listAttr.values = getReferenceItems(setting.referenceCode)
                    result.add(listAttr)
                } else {
                    result.add(FieldConfiguration(setting.fieldCode,
                            setting.name, setting.name, getControlType(setting.type)))
                }
            }
        }

        for (setting in listSettings.values) {
            result.add(getListConfiguration(setting))
        }

        return result
    }

    private fun getListConfiguration(settingList: List<FieldSetting>): ListFieldConfiguration {
        val s = settingList[0]
        val controlType = getControlType(s.type)
        val result = ListFieldConfiguration(controlType)
        result.caption = s.parentName
        result.fieldCode = s.fieldCode

        for (setting in settingList) {
            result.values.add(AttributeValue(setting.id, setting.name))
        }
        return result
    }

    private fun getControlType(sourceType: Int): Int {
        when (sourceType) {
            FieldSetting.TYPE_TEXT -> return FieldConfiguration.CONTROL_TYPE_TEXT
            FieldSetting.TYPE_BOOL_SIMPLE -> return FieldConfiguration.CONTROL_TYPE_BOOLEAN
            FieldSetting.TYPE_BOOL_SINGLE -> return FieldConfiguration.CONTROL_TYPE_RADIO_LIST
            FieldSetting.TYPE_INT -> return FieldConfiguration.CONTROL_TYPE_NUMBER
        }

        throw IllegalArgumentException("Unknown sourceType")
    }

    private fun getReferenceItems(reference: String): List<AttributeValue>? {
        try {
            return repository.getReferenceItems(reference)
        } catch (e: DataContextException) {
            toaster.showError("Error while getting reference items for reference code $reference", e)
            e.printStackTrace()
        }

        return null
    }


}
