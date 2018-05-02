package com.example.bl.ui

interface FirmEditableView : DynamicViewContainer {
    fun setTitle(title: String)

    fun showUndoButton(show: Boolean)

    fun closeView()

    fun refreshList()
}
