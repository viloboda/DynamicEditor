package com.example.model

fun String?.isNullOrEmpty(): Boolean {
    return this == null || this.isEmpty()
}