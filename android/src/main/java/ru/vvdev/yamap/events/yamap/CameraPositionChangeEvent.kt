package ru.vvdev.yamap.events.yamap

import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.events.Event

class CameraPositionChangeEvent(surfaceId: Int, viewId: Int, private val cameraPosition: WritableMap?)
    : Event<CameraPositionChangeEvent>(surfaceId, viewId) {

    override fun getEventName() = EVENT_NAME

    override fun getCoalescingKey(): Short = 0

    override fun getEventData() = cameraPosition

    companion object {
        const val EVENT_NAME = "topCameraPositionChange"
    }
}
