package com.example.bl.ui

interface DynamicAlertDialog {
    fun showDialog(message: String, leftButtonText: String, onLeftButtonClick: YoulaOnClickListener, rightButtonText: String, onRightButtonClick: YoulaOnClickListener)

    fun showDialog(message: String, rightButtonText: String, onRightButtonClick: YoulaOnClickListener)

    fun showDialog(message: Int, leftButtonText: Int, onLeftButtonClick: YoulaOnClickListener, rightButtonText: Int, onRightButtonClick: YoulaOnClickListener)

    fun showDialog(message: Int, rightButtonText: Int, onRightButtonClick: YoulaOnClickListener)

    fun enableRightButton(enable: Boolean)

    fun dismiss()

    fun setCancelable(cancelable: Boolean)

    fun setMessage(message: String)
}
