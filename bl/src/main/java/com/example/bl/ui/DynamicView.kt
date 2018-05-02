package com.example.bl.ui

import com.example.model.EditObjectTemplateDto
import com.example.model.FieldDto

interface DynamicView {
    val view: Any
    val attributeId: String

    fun getViewGroup(): DymamicViewGroup?

    fun setViewGroup(viewGroup: DymamicViewGroup)

    fun getValue(): FieldDto?

    fun setValue(value: FieldDto)

    fun isValid(): Boolean

    fun hasChanges(): Boolean

    fun setTemplate(templateItem: EditObjectTemplateDto.ItemDto)

    fun refresh()

    fun showWarning(warning: String)
}
