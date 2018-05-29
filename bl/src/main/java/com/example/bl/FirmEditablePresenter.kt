package com.example.bl

import com.example.bl.ui.FirmEditableView
import com.example.bl.ui.DynamicToaster
import com.example.dal.Repository
import com.example.model.FieldConfiguration
import com.example.model.EntityState
import com.example.model.FirmDto

class FirmEditablePresenter(attributesFactory: DynamicFieldsFactory,
                            attributesConfigurationService: FieldsConfigurationService,
                            templateManager: EditorTemplateManager,
                            locationManager: DymanicEditorLocationManager,
                            private val repository: Repository,
                            toaster: DynamicToaster) : DynamicViewPresenterBase<FirmDto, FirmEditableView>(attributesConfigurationService, attributesFactory, locationManager, toaster, templateManager) {

    override val dtoCopy: FirmDto?
        get() = if (dto.IsInAddingMode()) null else dto.copy()

    override fun onInit(configuration: List<FieldConfiguration>) {
        containerView.setTitle(if (dto.IsAddingMode) "Добавить фирму" else "Редактировать фирму")

        if (dto.State == EntityState.STATE_CHANGED || dto.State == EntityState.STATE_REMOVED) {
            containerView.showUndoButton(true)
        } else {
            containerView.showUndoButton(false)
        }
    }

    override fun closeView() {
        containerView.closeView()
    }

    override fun afterSaveObject() {
        closeView()
    }

    override fun saveObject(dto: FirmDto, initialDto: FirmDto?) {
        repository.saveObject(dto)
    }

    override fun undoChanges(dto: FirmDto, initialDto: FirmDto?) {
        // houseManager.undoChanges(dto)

        repository.undoChanges(dto)
        initialDto!!.copyTo(dto)
        dto.changeInfo = null
    }

    override fun getInitialDto(): FirmDto {
        return repository.getInitialVersion(dto)
    }

    fun onDeleteButtonClick() {
        repository.removeObject(dto)
        closeView()
    }
}
