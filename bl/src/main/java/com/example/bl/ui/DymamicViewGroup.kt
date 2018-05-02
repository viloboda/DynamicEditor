package com.example.bl.ui

interface DymamicViewGroup : DynamicView {

    val isEmpty: Boolean

    fun addView(view: DynamicView)

    fun removeAllViews()

    fun expand()
}
