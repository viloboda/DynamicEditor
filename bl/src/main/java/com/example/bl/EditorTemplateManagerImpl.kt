package com.example.bl

import com.example.dal.DataContextException
import com.example.dal.Repository
import com.example.model.EditObjectTemplateDto
import com.example.model.ObjectType
import com.example.model.TemplateDto
import java.util.ArrayList
import com.example.model.EditObjectTemplateDto.ItemDto
import com.example.model.FieldSetting


class EditorTemplateManagerImpl
constructor(private val repository: Repository) : EditorTemplateManager {

    override fun getEditTemplate(objectType: ObjectType): EditObjectTemplateDto {
        return when (objectType) {
            ObjectType.Firm -> getCardEditTemplate()
            else -> {
                throw IllegalArgumentException("objectType")
            }
        }
    }

    private fun getCardEditTemplate(): EditObjectTemplateDto {
        return getTemplateByType(TemplateDto.TemplateType.EditCard, getDefaultCardEditTemplateDto())
    }

    private fun getDefaultCardEditTemplateDto(): EditObjectTemplateDto {
        val items = ArrayList<ItemDto>(20)

        items.add(ItemDto(FieldSetting.FIELD_START_GROUP).apply {
            ShowHeader = true
            GroupName = "Фирма"
            BoldCaption = true
        })
        items.add(ItemDto(FieldSetting.FIELD_NAME).apply {
            Caption = "Название"
        })
        items.add(ItemDto(FieldSetting.FIELD_ADDRESS_NAME))
        items.add(ItemDto(FieldSetting.FIELD_LEGAL_NAME))
        items.add(ItemDto(FieldSetting.FIELD_LEGAL_FORM))
        items.add(ItemDto("*"))

        items.add(ItemDto(FieldSetting.FIELD_END_GROUP))

        return EditObjectTemplateDto("default", items)
    }

    private fun <T : TemplateDto> getTemplateByType(templateType: TemplateDto.TemplateType, defaultTemplate: T): T {
        try {
            val result = repository.getTemplatesByType(templateType, defaultTemplate.javaClass)

            val template = result.firstOrNull { x -> x.isDefault }
            if (template != null) {
                return template
            }

            return defaultTemplate

        } catch (e: DataContextException) {
            e.printStackTrace()
            return defaultTemplate
        }
    }
}
