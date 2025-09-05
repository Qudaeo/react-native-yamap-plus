package ru.yamap.view

import com.facebook.react.bridge.ReadableArray
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.viewmanagers.PolylineViewManagerDelegate
import com.facebook.react.viewmanagers.PolylineViewManagerInterface
import ru.yamap.events.YamapPolylinePressEvent
import ru.yamap.utils.PointUtil

class PolylineViewManager : ViewGroupManager<PolylineView>(), PolylineViewManagerInterface<PolylineView> {

    private val delegate = PolylineViewManagerDelegate(this)

    override fun getDelegate() = delegate

    override fun getName() = NAME

    override fun getExportedCustomBubblingEventTypeConstants() = mapOf(
        YamapPolylinePressEvent.EVENT_NAME to
                mapOf("phasedRegistrationNames" to mapOf("bubbled" to "onPress"))
    )

    override fun createViewInstance(context: ThemedReactContext) = PolylineView(context)

    // PROPS
    override fun setPoints(view: PolylineView, jsPoints: ReadableArray?) {
        val points = jsPoints?.let { PointUtil.jsPointsToPoints(it) }
        view.setPolygonPoints(points)
    }

    override fun setStrokeWidth(view: PolylineView, width: Float) {
        view.setStrokeWidth(width)
    }

    override fun setStrokeColor(view: PolylineView, color: Int) {
        view.setStrokeColor(color)
    }

    override fun setZI(view: PolylineView, zIndex: Float) {
        view.setZIndex(zIndex)
    }

    override fun setDashLength(view: PolylineView, length: Float) {
        view.setDashLength(length)
    }

    override fun setDashOffset(view: PolylineView, offset: Float) {
        view.setDashOffset(offset)
    }

    override fun setGapLength(view: PolylineView, length: Float) {
        view.setGapLength(length)
    }

    override fun setOutlineWidth(view: PolylineView, width: Float) {
        view.setOutlineWidth(width)
    }

    override fun setOutlineColor(view: PolylineView, color: Int) {
        view.setOutlineColor(color)
    }

    override fun setHandled(view: PolylineView, handled: Boolean) {
        view.setHandled(handled)
    }

    companion object {
        const val NAME = "PolylineView"
    }
}
