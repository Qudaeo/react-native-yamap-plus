package ru.yamap.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PointF
import android.view.View
import android.view.animation.LinearInterpolator
import com.facebook.react.views.view.ReactViewGroup
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.RotationType
import com.yandex.runtime.image.ImageProvider
import ru.yamap.models.ReactMapObject
import androidx.core.graphics.createBitmap
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.UIManagerHelper
import com.facebook.react.uimanager.UIManagerHelper.getSurfaceId
import ru.yamap.events.YamapMarkerPressEvent
import ru.yamap.utils.ImageCacheManager

class MarkerView(context: Context?) : ReactViewGroup(context), MapObjectTapListener,
    ReactMapObject {
    @JvmField
    var point: Point? = null
    private var _zIndex = 1f
    private var _scale = 1f
    private var _visible = true
    private var _handled = false
    private var _rotated = false
    private var _markerAnchor: PointF? = null
    private var _iconSource: String? = null
    private var _childView: View? = null
    override var rnMapObject: MapObject? = null
    private val _children = ArrayList<View>()
    init {
        // Fix for Fresco: Images require the view to be attached to the window to load.
        // We make the MarkerView invisible so it doesn't interfere with the map rendering,
        // but its children are still considered "attached".
        visibility = View.INVISIBLE
    }

    // OnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom -> updateMarker() }
    private val childLayoutListener =
        OnLayoutChangeListener { _, _, _, _, _, _, _, _, _ -> updateMarker() }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    }

    // PROPS
    fun setPoint(point: Point?) {
        this.point = point
        updateMarker()
    }

    fun setZIndex(zIndex: Float) {
        _zIndex = zIndex
        updateMarker()
    }

    fun setScale(scale: Float) {
        _scale = scale
        updateMarker()
    }

    fun setHandled(handled: Boolean) {
        _handled = handled
    }

    fun setRotated(rotated: Boolean) {
        _rotated = rotated
        updateMarker()
    }

    fun setVisible(visible: Boolean) {
        _visible = visible
        updateMarker()
    }

    fun setIconSource(source: String?) {
        _iconSource = source
        updateMarker()
    }

    fun setAnchor(anchor: PointF?) {
        _markerAnchor = anchor
        updateMarker()
    }

    private fun updateMarker() {
        if (rnMapObject != null && rnMapObject!!.isValid) {
            val iconStyle = IconStyle()
            iconStyle.scale = _scale
            iconStyle.rotationType = if (_rotated) RotationType.ROTATE else RotationType.NO_ROTATION
            iconStyle.visible = _visible
            if (_markerAnchor != null) {
                iconStyle.anchor = _markerAnchor
            }
            (rnMapObject as PlacemarkMapObject).geometry = point!!
            (rnMapObject as PlacemarkMapObject).zIndex = _zIndex
            (rnMapObject as PlacemarkMapObject).setIconStyle(iconStyle)

            if (_childView != null) {
                try {
                    if (_childView!!.width <= 0 || _childView!!.height <= 0) {
                        _childView!!.measure(
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                        )
                        _childView!!.layout(0, 0, _childView!!.measuredWidth, _childView!!.measuredHeight)
                    }

                    if (_childView!!.width > 0 && _childView!!.height > 0) {
                        val b = createBitmap(_childView!!.width, _childView!!.height)
                        val c = Canvas(b)
                        _childView!!.draw(c)
                        (rnMapObject as PlacemarkMapObject).setIcon(ImageProvider.fromBitmap(b))
                        (rnMapObject as PlacemarkMapObject).setIconStyle(iconStyle)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            if (_children.size == 0 && _iconSource != "") {
                _iconSource?.let { source ->
                    ImageCacheManager.getImage(context, source, fun (image: Bitmap?) {
                        ImageProvider.fromBitmap(image).let {
                            (rnMapObject as PlacemarkMapObject).setIcon(it)
                            (rnMapObject as PlacemarkMapObject).setIconStyle(iconStyle)
                        }
                    })
                }
            }
        }
    }

    fun setMarkerMapObject(obj: MapObject?) {
        rnMapObject = obj as PlacemarkMapObject?
        rnMapObject!!.addTapListener(this)
        updateMarker()
    }

    private fun setChildView(view: View?) {
        if (view == null) {
            _childView!!.removeOnLayoutChangeListener(childLayoutListener)
            _childView = null
            updateMarker()
            return
        }
        _childView = view
        _childView!!.addOnLayoutChangeListener(childLayoutListener)
    }

    fun addChildView(view: View, index: Int) {
        _children.add(index, view)
        // Add the child view to the hierarchy so it's attached to the window.
        // This allows libraries like Fresco to function correctly.
        addView(view, index)
        setChildView(_children[0])
    }

    fun removeChildView(index: Int) {
        _children.removeAt(index)
        removeViewAt(index)
        setChildView(if (_children.isNotEmpty()) _children[0] else null)
    }

    private fun moveAnimationLoop(lat: Double, lon: Double) {
        (rnMapObject as PlacemarkMapObject).geometry = Point(lat, lon)
    }

    private fun rotateAnimationLoop(delta: Float) {
        (rnMapObject as PlacemarkMapObject).direction = delta
    }

    fun animatedMoveTo(point: Point, duration: Float) {
        val p = (rnMapObject as PlacemarkMapObject).geometry
        val startLat = p.latitude
        val startLon = p.longitude
        val deltaLat = point.latitude - startLat
        val deltaLon = point.longitude - startLon
        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.setDuration(duration.toLong())
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.addUpdateListener { animation ->
            try {
                val v = animation.animatedFraction
                moveAnimationLoop(startLat + v * deltaLat, startLon + v * deltaLon)
            } catch (ex: Exception) {
                // I don't care atm..
            }
        }
        valueAnimator.start()
    }

    fun animatedRotateTo(angle: Float, duration: Float) {
        val placemark = (rnMapObject as PlacemarkMapObject)
        val startDirection = placemark.direction
        val delta = angle - placemark.direction
        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.setDuration(duration.toLong())
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.addUpdateListener { animation ->
            try {
                val v = animation.animatedFraction
                rotateAnimationLoop(startDirection + v * delta)
            } catch (ex: Exception) {
                // I don't care atm..
            }
        }
        valueAnimator.start()
    }

    override fun onMapObjectTap(mapObject: MapObject, point: Point): Boolean {
        val eventDispatcher = UIManagerHelper.getEventDispatcherForReactTag(context as ThemedReactContext, id)
        eventDispatcher?.dispatchEvent(YamapMarkerPressEvent(getSurfaceId(context), id))

        return _handled
    }
}
