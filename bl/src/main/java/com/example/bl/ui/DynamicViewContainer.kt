package com.example.bl.ui

interface DynamicViewContainer {
    val layoutInflater: Any

    fun onChangeView(view: DynamicView)

    fun inflateLayout(viewId: Int): Any

    fun inflateLayout(viewId: Int, attachToRoot: Boolean): Any

    fun addLayoutView(view: DynamicView)

    fun showCloseAlertDialog()

    fun showUndoAlertDialog()
}
