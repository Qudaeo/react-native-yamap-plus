package ru.yamap.view

import com.facebook.react.bridge.ReadableArray
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.viewmanagers.PolygonViewManagerDelegate
import com.facebook.react.viewmanagers.PolygonViewManagerInterface
import com.yandex.mapkit.geometry.Point
import ru.yamap.events.YamapPolygonPressEvent
import ru.yamap.utils.PointUtil

class PolygonViewManager : ViewGroupManager<PolygonView>(),  PolygonViewManagerInterface<PolygonView> {

    private val delegate = PolygonViewManagerDelegate(this)

    override fun getDelegate() = delegate

    override fun getName() = NAME

    override fun getExportedCustomBubblingEventTypeConstants() = mapOf(
        YamapPolygonPressEvent.EVENT_NAME to
                mapOf("phasedRegistrationNames" to mapOf("bubbled" to "onPress"))
    )

    override fun createViewInstance(context: ThemedReactContext) = PolygonView(context)

    // PROPS
    override fun setPoints(view: PolygonView, jsPoints: ReadableArray?) {
        jsPoints?.let {
            val points = PointUtil.jsPointsToPoints(it)
            view.setPolygonPoints(points)
        }
    }

    override fun setInnerRings(view: PolygonView, jsRings: ReadableArray?) {
        val rings = ArrayList<ArrayList<Point>>()
        jsRings?.let {
            for (j in 0 until it.size()) {
                val jsPoints = it.getArray(j) ?: return
                val points = PointUtil.jsPointsToPoints(jsPoints)
                rings.add(points)
            }
        }
        view.setPolygonInnerRings(rings)
    }

    override fun setStrokeWidth(view: PolygonView, width: Float) {
        view.setStrokeWidth(width)
    }

    override fun setStrokeColor(view: PolygonView, color: Int) {
        view.setStrokeColor(color)
    }

    override fun setFillColor(view: PolygonView, color: Int) {
        view.setFillColor(color)
    }

    override fun setZI(view: PolygonView, zIndex: Float) {
        view.setZIndex(zIndex)
    }

    override fun setHandled(view: PolygonView, handled: Boolean) {
        view.setHandled(handled)
    }

    companion object {
        const val NAME = "PolygonView"
    }
}
