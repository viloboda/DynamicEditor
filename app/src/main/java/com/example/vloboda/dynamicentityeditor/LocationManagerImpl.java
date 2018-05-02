package com.example.vloboda.dynamicentityeditor;

import com.example.bl.DymanicEditorLocationManager;
import com.example.model.ChangeInfoDto;
import com.example.model.GeoLocationDto;

public class LocationManagerImpl implements DymanicEditorLocationManager {
    @Override
    public GeoLocationDto getLocation() {
        return new GeoLocationDto(53.2, 86.5, 0.5F);
    }

    @Override
    public ChangeInfoDto getChangeInfo() {
        ChangeInfoDto result = new ChangeInfoDto();
        result.GeoLocation = getLocation();
        result.ChangeDate = System.currentTimeMillis();
        return new ChangeInfoDto();
    }
}
