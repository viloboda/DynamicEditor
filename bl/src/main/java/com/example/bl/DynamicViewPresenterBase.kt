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
                         private val fieldViewFactory: DynamicFieldsFactory,
                         private val locationManager: DymanicEditorLocationManager,
                         private val toaster: DynamicToaster,
                         private val templateManager: EditorTemplateManager) {

    private lateinit var views: MutableList<DynamicView>
    private lateinit var template: EditObjectTemplateDto
    internal lateinit var containerView: TView
    internal lateinit var dto: TDto

    private val removedFields = HashSet<String>()

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

        val processedFields = HashSet<String>(10)

        for (templateItem in this.template.Items) {
            if (templateItem.FieldCode == FieldSetting.FIELD_START_GROUP) {
                if (lastViewGroup != null) {
                    groupStack.push(lastViewGroup)
                }
                lastViewGroup = fieldViewFactory.getViewGroup(this.containerView, templateItem.GroupName)

                lastViewGroup.setTemplate(templateItem)
                views.add(lastViewGroup)
                continue
            }

            if (templateItem.FieldCode == FieldSetting.FIELD_END_GROUP) {
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
                    if (!processedFields.contains(config.fieldCode)) {
                        processField(lastViewGroup, commonTemplateItem, config)
                    }
                }
                continue
            }

            val config = getFieldConfiguration(configuration, templateItem.FieldCode)
                    ?: continue

            processedFields.add(config.fieldCode)
            processField(lastViewGroup, templateItem, config)
        }
    }

    private fun getFieldConfiguration(configuration: List<FieldConfiguration>, fieldCode: String): FieldConfiguration? {
        for (item in configuration) {
            if (item.fieldCode == fieldCode)
                return item
        }

        return null
    }

    private fun processField(lastViewGroup: DymamicViewGroup?, templateItem: EditObjectTemplateDto.ItemDto, config: FieldConfiguration) {
        val view = getDynamicView(templateItem, config)

        views.add(view)

        if (lastViewGroup != null) {
            lastViewGroup.addView(view)
        } else {
            this.containerView.addLayoutView(view)
        }
    }

    private fun getDynamicView(templateItem: EditObjectTemplateDto.ItemDto, config: FieldConfiguration): DynamicView {
        val view = fieldViewFactory.getView(this.containerView, config, templateItem)

        val field = this.dto.getField(config.fieldCode)
        if (field != null) {
            view.setValue(field)
        }

        view.setTemplate(templateItem)

        return view
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

        val fields = getFieldFromView(initialDto)
        dto.setFields(fields, removedFields)
        dto.changeInfo = locationManager.changeInfo

        fillRemovedFields(dto, initialDto)

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
            if (value.fieldCode == FieldSetting.FIELD_START_GROUP) {
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

    private fun getFieldFromView(initialDto: TDto?): List<FieldDto> {
        val fields = ArrayList<FieldDto>(views.size)
        removedFields.clear()

        for (value in views) {
            if (value.fieldCode == FieldSetting.FIELD_START_GROUP) {
                continue
            }

            val currentField = value.getValue() ?: continue

            if (currentField.isEmpty) {
                removedFields.add(value.fieldCode)
                continue
            }

            if (initialDto != null && initialDto.getField(currentField.code) == null) {
                currentField.State = EntityState.STATE_NEW
            }

            fields.add(currentField)
        }

        return fields
    }

    protected abstract fun afterSaveObject()

    @Throws(DataContextException::class)
    protected abstract fun saveObject(dto: TDto, initialDto: TDto?)

    @Throws(DataContextException::class)
    protected abstract fun undoChanges(dto: TDto, initialDto: TDto?)

    private fun fillRemovedFields(currentDto: TDto, initialDto: TDto?) {
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