package ru.vvdev.yamap.models;

import androidx.annotation.Nullable;

import com.yandex.mapkit.map.MapObject;

public interface ReactMapObject {
    @Nullable
    MapObject getMapObject();

    void setMapObject(@Nullable MapObject obj);
}