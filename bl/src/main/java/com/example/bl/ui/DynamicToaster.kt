package com.example.bl.ui

interface DynamicToaster {
    fun showError(error: String)

    fun showError(error: String, ex: Throwable)

    fun showLongMessage(message: String)
}
