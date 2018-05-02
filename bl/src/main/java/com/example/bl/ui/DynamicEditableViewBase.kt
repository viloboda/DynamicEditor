package com.example.bl.ui

import com.example.bl.DymanicEditorLocationManager
import com.example.model.ChangeInfoDto
import com.example.model.EntityState
import com.example.model.FieldDto

abstract class DynamicEditableViewBase(private val locationManager: DymanicEditorLocationManager) : DynamicView {

    protected var initialValue: FieldDto? = null

    private var changeInfo: ChangeInfoDto? = null

    private var viewGroup: DymamicViewGroup? = null

    protected abstract val currentValue: FieldDto?

    fun rememberChangeLocation() {
        if (changeInfo == null) {
            changeInfo = ChangeInfoDto()
        }
        changeInfo!!.ChangeDate = System.currentTimeMillis()
        changeInfo!!.GeoLocation = locationManager.location
    }

    override fun getValue(): FieldDto? {
        return setChangeInfo(currentValue)
    }

    override fun setViewGroup(viewGroup: DymamicViewGroup) {
        this.viewGroup = viewGroup
    }

    override fun getViewGroup(): DymamicViewGroup? {
        return viewGroup
    }

    private fun setChangeInfo(dto: FieldDto?): FieldDto? {
        if (dto == null) {
            return dto
        }

        if (initialValue == null) {
            dto.changeInfo = changeInfo
            dto.State = EntityState.STATE_NEW
            return dto
        }
        if (!comparer.equals(initialValue, dto)) {
            dto.changeInfo = changeInfo
            if (dto.State == EntityState.STATE_UNCHANGED) {
                dto.State = EntityState.STATE_CHANGED
            }
        } else {
            return initialValue
        }

        return dto
    }

    override fun hasChanges(): Boolean {
        val dto = currentValue
        if (initialValue == null && dto != null && !dto.isEmpty) {
            return true
        }

        return if (initialValue == null && dto != null && dto.isEmpty) {
            false
        } else !comparer.equals(initialValue, dto)

    }

    companion object {
        private val comparer = FieldDto.FieldDtoByValueEqualityComparer()
    }
}
