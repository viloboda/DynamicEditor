package com.example.bl.ui

import com.example.model.FieldConfiguration

interface ViewFactory {
    fun getView(container: DynamicViewContainer,
                configuration: FieldConfiguration): DynamicView
}

