package ru.vvdev.yamap

import android.view.View
import com.facebook.infer.annotation.Assertions
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.annotations.ReactProp
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import ru.vvdev.yamap.events.yamap.CameraPositionChangeEndEvent
import ru.vvdev.yamap.events.yamap.CameraPositionChangeEvent
import ru.vvdev.yamap.events.yamap.GetCameraPositionEvent
import ru.vvdev.yamap.events.yamap.GetVisibleRegionEvent
import ru.vvdev.yamap.view.YamapView
import javax.annotation.Nonnull

class YamapViewManager internal constructor() : ViewGroupManager<YamapView>() {
    override fun getName(): String {
        return REACT_CLASS
    }

    override fun getExportedCustomDirectEventTypeConstants(): Map<String, Any> {
        return mapOf()
    }

    override fun getExportedCustomBubblingEventTypeConstants(): MutableMap<String, Any> {
        return mutableMapOf(
            "routes" to
                    mapOf(
                        "phasedRegistrationNames" to
                                mapOf("bubbled" to "onRouteFound")
                    ),
            GetCameraPositionEvent.EVENT_NAME to
                    mapOf(
                        "phasedRegistrationNames" to
                                mapOf("bubbled" to "onCameraPositionReceived")
                    ),
            CameraPositionChangeEvent.EVENT_NAME to
                    mapOf(
                        "phasedRegistrationNames" to
                                mapOf("bubbled" to "onCameraPositionChange")
                    ),
            CameraPositionChangeEndEvent.EVENT_NAME to
                    mapOf(
                        "phasedRegistrationNames" to
                                mapOf("bubbled" to "onCameraPositionChangeEnd")
                    ),
            GetVisibleRegionEvent.EVENT_NAME to
                    mapOf(
                        "phasedRegistrationNames" to
                                mapOf("bubbled" to "onVisibleRegionReceived")
                    ),
            "onMapPress" to
                    mapOf("phasedRegistrationNames" to
                            mapOf("bubbled" to "onMapPress")
                    ),
            "onMapLongPress" to
                    mapOf(
                        "phasedRegistrationNames" to
                                mapOf("bubbled" to "onMapLongPress")
                    ),
            "onMapLoaded" to
                    mapOf(
                        "phasedRegistrationNames" to
                                mapOf("bubbled" to "onMapLoaded")
                    ),
            "screenToWorldPoints" to
                    mapOf(
                        "phasedRegistrationNames" to
                                mapOf("bubbled" to "onScreenToWorldPointsReceived")
                    ),
            "worldToScreenPoints" to
                    mapOf(
                        "phasedRegistrationNames" to
                                mapOf("bubbled" to "onWorldToScreenPointsReceived")
                    ),
            )
    }

    override fun getCommandsMap(): Map<String, Int> {
        return mapOf(
            "setCenter" to SET_CENTER,
            "fitAllMarkers" to FIT_ALL_MARKERS,
            "findRoutes" to FIND_ROUTES,
            "setZoom" to SET_ZOOM,
            "getCameraPosition" to GET_CAMERA_POSITION,
            "getVisibleRegion" to GET_VISIBLE_REGION,
            "setTrafficVisible" to SET_TRAFFIC_VISIBLE,
            "fitMarkers" to FIT_MARKERS,
            "getScreenPoints" to GET_SCREEN_POINTS,
            "getWorldPoints" to GET_WORLD_POINTS,
        )
    }

    override fun receiveCommand(
        view: YamapView,
        commandType: String,
        args: ReadableArray?
    ) {
        Assertions.assertNotNull(view)
        Assertions.assertNotNull(args)

        when (commandType) {
            "setCenter" -> setCenter(
                castToYaMapView(view),
                args!!.getMap(0),
                args.getDouble(1).toFloat(),
                args.getDouble(2).toFloat(),
                args.getDouble(3).toFloat(),
                args.getDouble(4).toFloat(),
                args.getInt(5)
            )

            "fitAllMarkers" -> fitAllMarkers(view)
            "fitMarkers" -> if (args != null) {
                fitMarkers(view, args.getArray(0))
            }

            "findRoutes" -> if (args != null) {
                findRoutes(view, args.getArray(0), args.getArray(1), args.getString(2))
            }

            "setZoom" -> if (args != null) {
                view.setZoom(
                    args.getDouble(0).toFloat(),
                    args.getDouble(1).toFloat(),
                    args.getInt(2)
                )
            }

            "getCameraPosition" -> if (args != null) {
                view.emitCameraPositionToJS(args.getString(0))
            }

            "getVisibleRegion" -> if (args != null) {
                view.emitVisibleRegionToJS(args.getString(0))
            }

            "setTrafficVisible" -> if (args != null) {
                view.setTrafficVisible(args.getBoolean(0))
            }

            "getScreenPoints" -> if (args != null) {
                view.emitWorldToScreenPoints(args.getArray(0), args.getString(1))
            }

            "getWorldPoints" -> if (args != null) {
                view.emitScreenToWorldPoints(args.getArray(0), args.getString(1))
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

    private fun castToYaMapView(view: View): YamapView {
        return view as YamapView
    }

    @Nonnull
    public override fun createViewInstance(@Nonnull context: ThemedReactContext): YamapView {
        val view = YamapView(context)
        MapKitFactory.getInstance().onStart()
        view.onStart()

        return view
    }

    private fun setCenter(
        view: YamapView,
        center: ReadableMap?,
        zoom: Float,
        azimuth: Float,
        tilt: Float,
        duration: Float,
        animation: Int
    ) {
        if (center != null) {
            val centerPosition = Point(center.getDouble("lat"), center.getDouble("lon"))
            val pos = CameraPosition(centerPosition, zoom, azimuth, tilt)
            view.setCenter(pos, duration, animation)
        }
    }

    private fun fitAllMarkers(view: View) {
        castToYaMapView(view).fitAllMarkers()
    }

    private fun fitMarkers(view: View, jsPoints: ReadableArray?) {
        if (jsPoints != null) {
            val points = ArrayList<Point?>()

            for (i in 0 until jsPoints.size()) {
                val point = jsPoints.getMap(i)
                points.add(Point(point.getDouble("lat"), point.getDouble("lon")))
            }

            castToYaMapView(view).fitMarkers(points)
        }
    }

    private fun findRoutes(
        view: View,
        jsPoints: ReadableArray?,
        jsVehicles: ReadableArray?,
        id: String?
    ) {
        if (jsPoints != null) {
            val points = ArrayList<Point?>()

            for (i in 0 until jsPoints.size()) {
                val point = jsPoints.getMap(i)
                points.add(Point(point.getDouble("lat"), point.getDouble("lon")))
            }

            val vehicles = ArrayList<String>()

            if (jsVehicles != null) {
                for (i in 0 until jsVehicles.size()) {
                    vehicles.add(jsVehicles.getString(i))
                }
            }

            castToYaMapView(view).findRoutes(points, vehicles, id)
        }
    }

    // PROPS
    @ReactProp(name = "userLocationIcon")
    fun setUserLocationIcon(view: View, icon: String?) {
        if (icon != null) {
            castToYaMapView(view).setUserLocationIcon(icon)
        }
    }

    @ReactProp(name = "userLocationIconScale")
    fun setUserLocationIconScale(view: View, scale: Float) {
        castToYaMapView(view).setUserLocationIconScale(scale)
    }

    @ReactProp(name = "userLocationAccuracyFillColor")
    fun setUserLocationAccuracyFillColor(view: View, color: Int) {
        castToYaMapView(view).setUserLocationAccuracyFillColor(color)
    }

    @ReactProp(name = "userLocationAccuracyStrokeColor")
    fun setUserLocationAccuracyStrokeColor(view: View, color: Int) {
        castToYaMapView(view).setUserLocationAccuracyStrokeColor(color)
    }

    @ReactProp(name = "userLocationAccuracyStrokeWidth")
    fun setUserLocationAccuracyStrokeWidth(view: View, width: Float) {
        castToYaMapView(view).setUserLocationAccuracyStrokeWidth(width)
    }

    @ReactProp(name = "showUserPosition")
    fun setShowUserPosition(view: View, show: Boolean?) {
        castToYaMapView(view).setShowUserPosition(show!!)
    }

    @ReactProp(name = "nightMode")
    fun setNightMode(view: View, nightMode: Boolean?) {
        castToYaMapView(view).setNightMode(nightMode ?: false)
    }

    @ReactProp(name = "scrollGesturesEnabled")
    fun setScrollGesturesEnabled(view: View, scrollGesturesEnabled: Boolean) {
        castToYaMapView(view).setScrollGesturesEnabled(scrollGesturesEnabled)
    }

    @ReactProp(name = "rotateGesturesEnabled")
    fun setRotateGesturesEnabled(view: View, rotateGesturesEnabled: Boolean) {
        castToYaMapView(view).setRotateGesturesEnabled(rotateGesturesEnabled)
    }

    @ReactProp(name = "zoomGesturesEnabled")
    fun setZoomGesturesEnabled(view: View, zoomGesturesEnabled: Boolean) {
        castToYaMapView(view).setZoomGesturesEnabled(zoomGesturesEnabled)
    }

    @ReactProp(name = "tiltGesturesEnabled")
    fun setTiltGesturesEnabled(view: View, tiltGesturesEnabled: Boolean) {
        castToYaMapView(view).setTiltGesturesEnabled(tiltGesturesEnabled)
    }

    @ReactProp(name = "fastTapEnabled")
    fun setFastTapEnabled(view: View, fastTapEnabled: Boolean) {
        castToYaMapView(view).setFastTapEnabled(fastTapEnabled)
    }

    @ReactProp(name = "mapStyle")
    fun setMapStyle(view: View, style: String?) {
        if (style != null) {
            castToYaMapView(view).setMapStyle(style)
        }
    }

    @ReactProp(name = "mapType")
    fun setMapType(view: View, type: String?) {
        if (type != null) {
            castToYaMapView(view).setMapType(type)
        }
    }

    @ReactProp(name = "initialRegion")
    fun setInitialRegion(view: View, params: ReadableMap?) {
        if (params != null) {
            castToYaMapView(view).setInitialRegion(params)
        }
    }

    @ReactProp(name = "interactive")
    fun setInteractive(view: View, interactive: Boolean) {
        castToYaMapView(view).setInteractive(interactive)
    }

    @ReactProp(name = "logoPosition")
    fun setLogoPosition(view: View, params: ReadableMap?) {
        if (params != null) {
            castToYaMapView(view).setLogoPosition(params)
        }
    }

    @ReactProp(name = "logoPadding")
    fun setLogoPadding(view: View, params: ReadableMap?) {
        if (params != null) {
            castToYaMapView(view).setLogoPadding(params)
        }
    }

    override fun addView(parent: YamapView, child: View, index: Int) {
        parent.addFeature(child, index)
        super.addView(parent, child, index)
    }

    override fun removeViewAt(parent: YamapView, index: Int) {
        parent.removeChild(index)
        super.removeViewAt(parent, index)
    }

    companion object {
        const val REACT_CLASS: String = "YamapView"

        private const val SET_CENTER = 1
        private const val FIT_ALL_MARKERS = 2
        private const val FIND_ROUTES = 3
        private const val SET_ZOOM = 4
        private const val GET_CAMERA_POSITION = 5
        private const val GET_VISIBLE_REGION = 6
        private const val SET_TRAFFIC_VISIBLE = 7
        private const val FIT_MARKERS = 8
        private const val GET_SCREEN_POINTS = 9
        private const val GET_WORLD_POINTS = 10
    }
}
