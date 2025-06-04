package ru.yamap.events.yamap

import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.events.Event
import com.yandex.mapkit.geometry.Point
import ru.yamap.utils.PointUtil

class YamapLongPressEvent(surfaceId: Int, viewId: Int, private val point: Point) : Event<YamapLongPressEvent>(surfaceId, viewId) {
    override fun getEventName(): String {
        return EVENT_NAME
    }
    override fun getCoalescingKey(): Short {
        // All events for a given view can be coalesced.
        return 0
    }

    override fun getEventData(): WritableMap {
        return PointUtil.pointToJsPoint(point)
    }

    companion object {
        const val EVENT_NAME = "topMapLongPress"
    }
}
