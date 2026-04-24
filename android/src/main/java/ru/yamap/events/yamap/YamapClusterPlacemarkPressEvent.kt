package ru.yamap.events.yamap

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.events.Event

class YamapClusterPlacemarkPressEvent(
    surfaceId: Int,
    viewId: Int,
    private val lat: Double,
    private val lon: Double,
    private val index: Int,
) : Event<YamapClusterPlacemarkPressEvent>(surfaceId, viewId) {
    override fun getEventName() = EVENT_NAME

    override fun getCoalescingKey(): Short = 0

    override fun getEventData(): WritableMap = Arguments.createMap().apply {
        putDouble("lat", lat)
        putDouble("lon", lon)
        putInt("index", index)
    }

    companion object {
        const val EVENT_NAME = "topClusterPlacemarkPress"
    }
}
