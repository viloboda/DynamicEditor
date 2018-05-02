package com.example.bl

import com.example.bl.ui.DymamicViewGroup
import com.example.bl.ui.DynamicView
import com.example.bl.ui.DynamicViewContainer
import com.example.bl.ui.DynamicToaster
import com.example.dal.DataContextException
import com.example.model.FieldConfiguration
import com.example.model.EditObjectTemplateDto
import com.example.model.EntityState
import com.example.model.FieldDto
import com.example.model.FieldSetting
import com.example.model.SimpleDto

import java.util.ArrayList
import java.util.HashSet
import java.util.Stack

abstract class DynamicViewPresenterBase<TDto : SimpleDto, TView : DynamicViewContainer>
    internal constructor(private val fieldsConfigurationService: FieldsConfigurationService,
                         private val attributesFactory: DynamicFieldsFactory,
                         private val locationManager: DymanicEditorLocationManager,
                         private val toaster: DynamicToaster,
                         private val templateManager: EditorTemplateManager) {

    private lateinit var views: MutableList<DynamicView>
    private lateinit var template: EditObjectTemplateDto
    internal lateinit var containerView: TView
    internal lateinit var dto: TDto

    private val removedAttributes = HashSet<String>()

    private val editTemplate: EditObjectTemplateDto
        get() = templateManager.getEditTemplate(dto.objectType)

    protected abstract val dtoCopy: TDto?

    @Throws(DataContextException::class)
    protected abstract fun getInitialDto(): TDto

    fun init(dto: TDto, containerView: TView) {
        this.containerView = containerView
        this.dto = dto

        val configuration = getFieldsConfiguration(fieldsConfigurationService)

        this.template = editTemplate

        this.views = ArrayList(configuration.size)
        addDynamicViews(configuration)

        onInit(configuration)
    }

    private fun getFieldsConfiguration(fieldsConfigurationService: FieldsConfigurationService): List<FieldConfiguration> {
        return fieldsConfigurationService.getConfiguration(dto.objectType)
    }

    protected abstract fun onInit(configuration: List<FieldConfiguration>)

    private fun onSaveError(ex: DataContextException) {
        toaster.showError("Error while saving object: " + ex.message, ex)
    }

    private fun onGetInitialDtoError(ex: DataContextException) {
        toaster.showError("Error while getting initial version: " + ex.message, ex)
    }

    private fun addDynamicViews(configuration: List<FieldConfiguration>) {
        val groupStack = Stack<DymamicViewGroup>()
        var lastViewGroup: DymamicViewGroup? = null

        val processedAttributes = HashSet<String>(10)

        for (templateItem in this.template.Items) {
            if (templateItem.FieldCode == FieldSetting.ATTRIBUTE_START_GROUP) {
                if (lastViewGroup != null) {
                    groupStack.push(lastViewGroup)
                }
                lastViewGroup = attributesFactory.getViewGroup(this.containerView, templateItem.GroupName)

                lastViewGroup.setTemplate(templateItem)
                views.add(lastViewGroup)
                continue
            }

            if (templateItem.FieldCode == FieldSetting.ATTRIBUTE_END_GROUP) {
                if (groupStack.empty()) {
                    if (!lastViewGroup!!.isEmpty) {
                        this.containerView.addLayoutView(lastViewGroup)
                    }
                } else {
                    groupStack.peek().addView(lastViewGroup!!)
                }
                lastViewGroup = if (groupStack.empty()) null else groupStack.pop()
                continue
            }

            if (templateItem.FieldCode == "*") {
                val commonTemplateItem = this.template.getTemplateItem(templateItem.FieldCode)
                        ?: continue

                for (config in configuration) {
                    if (!processedAttributes.contains(config.attributeId)) {
                        processAttribute(lastViewGroup, commonTemplateItem, config)
                    }
                }
                continue
            }

            val config = getAttributeConfiguration(configuration, templateItem.FieldCode)
                    ?: continue

            processedAttributes.add(config.attributeId)
            processAttribute(lastViewGroup, templateItem, config)
        }
    }

    private fun getAttributeConfiguration(configuration: List<FieldConfiguration>, fieldCode: String): FieldConfiguration? {
        for (item in configuration) {
            if (item.attributeId == fieldCode)
                return item
        }

        return null
    }

    private fun processAttribute(lastViewGroup: DymamicViewGroup?, templateItem: EditObjectTemplateDto.ItemDto, config: FieldConfiguration) {
        val view = getDynamicView(templateItem, config)

        views.add(view)

        if (lastViewGroup != null) {
            lastViewGroup.addView(view)
        } else {
            this.containerView.addLayoutView(view)
        }
    }

    private fun getDynamicView(templateItem: EditObjectTemplateDto.ItemDto, config: FieldConfiguration): DynamicView {
        val view = attributesFactory.getView(this.containerView, config, templateItem)

        val attribute = this.dto.getField(config.attributeId)
        if (attribute != null) {
            setViewValueFromAttribute(view, attribute)
        }

        view.setTemplate(templateItem)

        return view
    }

    private fun setViewValueFromAttribute(view: DynamicView, attribute: FieldDto) {
        view.setValue(attribute)
    }

    fun onOkClick() {

        var initialDto: TDto? = null
        val beforeSaveDtoCopy = dtoCopy
        try {
            if (!dto.IsInAddingMode()) {
                initialDto = getInitialDto()
            }
        } catch (e: DataContextException) {
            onGetInitialDtoError(e)
            return
        }

        val attributes = getAttributesFromView(initialDto)
        dto.setFields(attributes, removedAttributes)
        dto.changeInfo = locationManager.changeInfo

        fillRemovedAttributes(dto, initialDto)

        try {
            val hasChanges = dto.IsInAddingMode() || initialDto != dto
            if (hasChanges || beforeSaveDtoCopy != null && beforeSaveDtoCopy != dto) {

                if (!hasChanges) {
                    dto.changeInfo = null
                }

                saveObject(dto, initialDto)
            } else {
                undoChanges(dto, initialDto)
            }

            afterSaveObject()
        } catch (e: DataContextException) {
            onSaveError(e)
        }

    }

    fun onUndoChangesClick() {
        containerView.showUndoAlertDialog()
    }

    fun undoChanges() {
        try {
            if (!dto.IsInAddingMode()) {
                val initialDto = getInitialDto()
                undoChanges(dto, initialDto)
                closeView()
            }
        } catch (e: DataContextException) {
            onGetInitialDtoError(e)
        }

    }

    abstract fun closeView()

    fun onBackButtonClick() {
        var hasChanges = false
        for (value in views) {
            if (value.attributeId == FieldSetting.ATTRIBUTE_START_GROUP) {
                continue
            }
            if (value.hasChanges()) {
                hasChanges = true
                break
            }
        }

        if (!hasChanges) {
            closeView()
            return
        }

        containerView.showCloseAlertDialog()
    }

    private fun getAttributesFromView(initialDto: TDto?): List<FieldDto> {
        val attributes = ArrayList<FieldDto>(views.size)
        removedAttributes.clear()

        for (value in views) {
            if (value.attributeId == FieldSetting.ATTRIBUTE_START_GROUP) {
                continue
            }

            val currentField = value.getValue() ?: continue

            if (currentField.isEmpty) {
                removedAttributes.add(value.attributeId)
                continue
            }

            if (initialDto != null && initialDto.getField(currentField.code) == null) {
                currentField.State = EntityState.STATE_NEW
            }

            attributes.add(currentField)
        }

        return attributes
    }

    protected abstract fun afterSaveObject()

    @Throws(DataContextException::class)
    protected abstract fun saveObject(dto: TDto, initialDto: TDto?)

    @Throws(DataContextException::class)
    protected abstract fun undoChanges(dto: TDto, initialDto: TDto?)

    private fun fillRemovedAttributes(currentDto: TDto, initialDto: TDto?) {
        if (initialDto == null) {
            return
        }

        initialDto.clearRemoveFields()

        for (field in initialDto.fields) {
            if (currentDto.getField(field.code) == null) {
                field.setRemovedState()
                field.changeInfo = locationManager.changeInfo
                currentDto.addRemovedField(field)
            }
        }

        currentDto.clearRemoveFields()
    }
}