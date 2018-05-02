package com.example.bl.ui

import com.example.bl.DymanicEditorLocationManager

abstract class ViewFactoryBase(private val commonServices: UICommonServices) : ViewFactory {

    val locationManager: DymanicEditorLocationManager
        get() = this.commonServices.locationManager

    val toaster: DynamicToaster
        get() = this.commonServices.toaster
}
