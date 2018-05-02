package com.example.bl

import com.example.model.ChangeInfoDto
import com.example.model.GeoLocationDto

interface DymanicEditorLocationManager {
    val location: GeoLocationDto

    val changeInfo: ChangeInfoDto
}
