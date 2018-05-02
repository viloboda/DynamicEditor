package com.example.bl.ui

import com.example.model.EditObjectTemplateDto
import com.example.model.FieldDto

interface DynamicView {
    val view: Any
    val fieldCode: String

    fun getViewGroup(): DymamicViewGroup?

    fun setViewGroup(viewGroup: DymamicViewGroup)

    fun getValue(): FieldDto?

    fun setValue(value: FieldDto)

    fun hasChanges(): Boolean

    fun setTemplate(templateItem: EditObjectTemplateDto.ItemDto)
}
