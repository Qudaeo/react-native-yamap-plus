package ru.yamap.view

import android.view.View
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.viewmanagers.MarkerViewManagerDelegate
import com.facebook.react.viewmanagers.MarkerViewManagerInterface
import ru.yamap.events.YamapMarkerPressEvent
import ru.yamap.utils.PointUtil

class MarkerViewManager : ViewGroupManager<MarkerView>(), MarkerViewManagerInterface<MarkerView> {

    private val delegate = MarkerViewManagerDelegate(this)

    override fun getDelegate() = delegate

    override fun getName() = NAME

    override fun getExportedCustomBubblingEventTypeConstants() = mapOf(
        YamapMarkerPressEvent.EVENT_NAME to
                mapOf("phasedRegistrationNames" to mapOf("bubbled" to "onPress"))
    )

    public override fun createViewInstance(context: ThemedReactContext) = MarkerView(context)

    // PROPS
    override fun setPoint(view: MarkerView, jsPoint: ReadableMap?) {
        jsPoint?.let {
            val point = PointUtil.readableMapToPoint(it)
            view.setPoint(point)
        }
    }

    override fun setZI(view: MarkerView, zIndex: Float) {
        view.setZIndex(zIndex)
    }

    override fun setScale(view: MarkerView, scale: Float) {
        view.setScale(scale)
    }

    override fun setHandled(view: MarkerView, handled: Boolean) {
        view.setHandled(handled)
    }

    override fun setRotated(view: MarkerView, rotated: Boolean) {
        view.setRotated(rotated)
    }

    override fun setVisible(view: MarkerView, visible: Boolean) {
        view.setVisible(visible)
    }

    override fun setSource(view: MarkerView, source: String?) {
        if (source != null) {
            view.setIconSource(source)
        }
    }

    override fun setAnchor(view: MarkerView, anchor: ReadableMap?) {
        val pointF = PointUtil.jsPointToPointF(anchor)
        view.setAnchor(pointF)
    }

    override fun addView(parent: MarkerView, child: View, index: Int) {
        parent.addChildView(child, index)
    }

    override fun removeViewAt(parent: MarkerView, index: Int) {
        parent.removeChildView(index)
    }

    override fun receiveCommand(view: MarkerView, commandType: String, argsArr: ReadableArray?) {
        if (commandType == "updateMarker") {
            view.onUpdateMarker()
            return
        }

        val args = argsArr?.getArray(0)?.getMap(0) ?: return

        when (commandType) {
            "animatedMoveTo" -> {
                val jsPoint = args.getMap("coords") ?: return
                val moveDuration = args.getDouble("duration")
                val point = PointUtil.readableMapToPoint(jsPoint)
                view.animatedMoveTo(point, moveDuration.toFloat())
            }

            "animatedRotateTo" -> {
                val angle = args.getDouble("angle")
                val rotateDuration = args.getDouble("duration")
                view.animatedRotateTo(angle.toFloat(), rotateDuration.toFloat())
            }

            else -> throw IllegalArgumentException(
                String.format(
                    "Unsupported command %d received by %s.",
                    commandType,
                    javaClass.simpleName
                )
            )
        }
    }

    companion object {
        const val NAME = "MarkerView"
    }
}
