package ru.vvdev.yamap.events.yamap

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.events.Event
import com.yandex.mapkit.map.MapLoadStatistics

class MapLoadedEvent(surfaceId: Int, viewId: Int, private val statistics: MapLoadStatistics) : Event<MapLoadedEvent>(surfaceId, viewId) {
    override fun getEventName(): String {
        return EVENT_NAME
    }
    override fun getCoalescingKey(): Short {
        // All events for a given view can be coalesced.
        return 0
    }

    override fun getEventData(): WritableMap? {
        val data = Arguments.createMap()
        data.putInt("renderObjectCount", statistics.renderObjectCount)
        data.putDouble("curZoomModelsLoaded", statistics.curZoomModelsLoaded.toDouble())
        data.putDouble("curZoomPlacemarksLoaded", statistics.curZoomPlacemarksLoaded.toDouble())
        data.putDouble("curZoomLabelsLoaded", statistics.curZoomLabelsLoaded.toDouble())
        data.putDouble("curZoomGeometryLoaded", statistics.curZoomGeometryLoaded.toDouble())
        data.putDouble("tileMemoryUsage", statistics.tileMemoryUsage.toDouble())
        data.putDouble("delayedGeometryLoaded", statistics.delayedGeometryLoaded.toDouble())
        data.putDouble("fullyAppeared", statistics.fullyAppeared.toDouble())
        data.putDouble("fullyLoaded", statistics.fullyLoaded.toDouble())
        return data
    }

    companion object {
        const val EVENT_NAME = "topMapLoaded"
    }
}
