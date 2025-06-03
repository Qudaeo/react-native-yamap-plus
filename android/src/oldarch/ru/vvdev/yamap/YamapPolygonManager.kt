package ru.vvdev.yamap

import com.facebook.react.bridge.ReadableArray
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.annotations.ReactProp
import ru.vvdev.yamap.view.PolygonView

class YamapPolygonManager : ViewGroupManager<PolygonView>() {

    private val implementation = YamapPolygonImpl()

    override fun getName() = YamapPolygonImpl.NAME

    override fun getExportedCustomDirectEventTypeConstants() =
        implementation.getExportedCustomDirectEventTypeConstants()

    override fun createViewInstance(context: ThemedReactContext) = PolygonView(context)

    // PROPS
    @ReactProp(name = "points")
    fun setPoints(view: PolygonView, jsPoints: ReadableArray?) {
        implementation.setPoints(view, jsPoints)
    }

    @ReactProp(name = "innerRings")
    fun setInnerRings(view: PolygonView, jsRings: ReadableArray?) {
        implementation.setInnerRings(view, jsRings)
    }

    @ReactProp(name = "strokeWidth")
    fun setStrokeWidth(view: PolygonView, width: Float) {
        implementation.setStrokeWidth(view, width)
    }

    @ReactProp(name = "strokeColor")
    fun setStrokeColor(view: PolygonView, color: Int) {
        implementation.setStrokeColor(view, color)
    }

    @ReactProp(name = "fillColor")
    fun setFillColor(view: PolygonView, color: Int) {
        implementation.setFillColor(view, color)
    }

    @ReactProp(name = "zI")
    fun setZI(view: PolygonView, zIndex: Float) {
        implementation.setZI(view, zIndex)
    }

    @ReactProp(name = "handled")
    fun setHandled(view: PolygonView, handled: Boolean) {
        implementation.setHandled(view, handled)
    }
}
