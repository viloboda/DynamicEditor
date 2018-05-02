package com.example.bl

import com.example.bl.ui.DymamicViewGroup
import com.example.bl.ui.DynamicView
import com.example.bl.ui.DynamicViewContainer
import com.example.model.FieldConfiguration
import com.example.model.EditObjectTemplateDto

interface DynamicFieldsFactory {
    fun getViewGroup(container: DynamicViewContainer, groupName: String?): DymamicViewGroup

    fun getView(container: DynamicViewContainer, config: FieldConfiguration, templateItem: EditObjectTemplateDto.ItemDto): DynamicView
}
