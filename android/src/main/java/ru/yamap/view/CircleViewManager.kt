package ru.yamap.view

import com.facebook.react.bridge.ReadableMap
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.viewmanagers.CircleViewManagerDelegate
import com.facebook.react.viewmanagers.CircleViewManagerInterface
import ru.yamap.events.YamapCirclePressEvent
import ru.yamap.utils.PointUtil

class CircleViewManager : ViewGroupManager<CircleView>(), CircleViewManagerInterface<CircleView> {

    private val delegate = CircleViewManagerDelegate(this)

    override fun getDelegate() = delegate

    override fun getName() = NAME

    override fun getExportedCustomBubblingEventTypeConstants() = mapOf(
        YamapCirclePressEvent.EVENT_NAME to
                mapOf("phasedRegistrationNames" to mapOf("bubbled" to "onPress"))
    )

    override fun createViewInstance(context: ThemedReactContext)  = CircleView(context)

    override fun setCenter(view: CircleView, center: ReadableMap?) {
        center?.let {
            val point = PointUtil.readableMapToPoint(it)
            view.setCenter(point)
        }
    }

    override fun setRadius(view: CircleView, radius: Float) {
        view.setRadius(radius)
    }

    override fun setStrokeWidth(view: CircleView, width: Float) {
        view.setStrokeWidth(width)
    }

    override fun setStrokeColor(view: CircleView, color: Int) {
        view.setStrokeColor(color)
    }

    override fun setFillColor(view: CircleView, color: Int) {
        view.setFillColor(color)
    }

    override fun setZI(view: CircleView, zIndex: Float) {
        view.setZIndex(zIndex)
    }

    override fun setHandled(view: CircleView, handled: Boolean) {
        view.setHandled(handled)
    }

    companion object {
        const val NAME = "CircleView"
    }
}
