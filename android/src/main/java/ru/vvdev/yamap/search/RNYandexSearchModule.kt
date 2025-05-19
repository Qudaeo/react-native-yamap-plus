package ru.vvdev.yamap.search

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.UiThreadUtil
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.LinearRing
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polygon
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.SearchType
import com.yandex.mapkit.search.Snippet
import ru.vvdev.yamap.utils.Callback
import ru.vvdev.yamap.utils.PointUtil

class RNYandexSearchModule(reactContext: ReactApplicationContext?) :
    ReactContextBaseJavaModule(reactContext) {
    private var searchClient: MapSearchClient? = null
    private val searchArgsHelper = YandexSearchRNArgsHelper()

    override fun getName(): String {
        return "YamapSearch"
    }

    private fun getGeometry(figure: ReadableMap?): Geometry {
        if (figure?.getMap("value")!=null && figure.getString("type") !=null) {
            return when (figure.getString("type")) {
                "POINT" -> {
                    val jsPoint = figure.getMap("value")
                    val point = jsPoint?.let { PointUtil.readableMapToPoint(it) }
                    Geometry.fromPoint(point ?: Point(0.0, 0.0))
                }

                "BOUNDINGBOX" -> {
                    val value = figure.getMap("value") ?:
                    return Geometry.fromPoint(Point(0.0, 0.0))

                    val southWestJsPoint = value.getMap("southWest")
                    val southWestPoint = southWestJsPoint?.let { PointUtil.readableMapToPoint(it) }

                    val northEastJsPoint = value.getMap("northEast")
                    val northEastPoint = northEastJsPoint?.let { PointUtil.readableMapToPoint(it) }

                    Geometry.fromBoundingBox(BoundingBox(
                        southWestPoint ?: Point(0.0, 0.0),
                        northEastPoint ?: Point(0.0, 0.0)
                    ))
                }

                "POLYLINE" -> {
                    val value = figure.getMap("value") ?:
                    return Geometry.fromPoint(Point(0.0, 0.0))

                    val jsPoints = value.getArray("points") ?:
                    return Geometry.fromPoint(Point(0.0, 0.0))

                    val points = PointUtil.jsPointsToPoints(jsPoints)
                    Geometry.fromPolyline(Polyline(points))
                }

                "POLYGON" -> {
                    val value = figure.getMap("value") ?:
                    return Geometry.fromPoint(Point(0.0, 0.0))

                    val jsPoints = value.getArray("points") ?:
                    return Geometry.fromPoint(Point(0.0, 0.0))

                    val points = PointUtil.jsPointsToPoints(jsPoints)
                    Geometry.fromPolygon(Polygon(LinearRing(points), ArrayList()));
                }

                else -> Geometry.fromPoint(Point(0.0, 0.0))
            }
        }
        return Geometry.fromPoint(Point(0.0, 0.0))
    }

    private fun getSearchOptions(options: ReadableMap?): SearchOptions {
        return if (options!=null) {
            SearchOptions().apply {
                searchTypes = if (options.hasKey("searchTypes")) options.getInt("searchTypes") else SearchType.NONE.value
                snippets = if (options.hasKey("snippets")) options.getInt("snippets") else Snippet.NONE.value
                geometry = if (options.hasKey("geometry")) options.getBoolean("geometry") else false
                disableSpellingCorrection = if (options.hasKey("disableSpellingCorrection")) options.getBoolean("disableSpellingCorrection") else false
            }
        } else {
            SearchOptions();
        }
    }

    @ReactMethod
    fun searchByAddress(searchQuery: String?, figure: ReadableMap?, options: ReadableMap?, promise: Promise) {
        if (searchQuery != null) {
            val searchOptions = getSearchOptions(options)
            UiThreadUtil.runOnUiThread {
                getSearchClient().searchAddress(searchQuery, getGeometry(figure), searchOptions,
                    object : Callback<MapSearchItem?> {
                        override fun invoke(arg: MapSearchItem?) {
                            promise.resolve(searchArgsHelper.createSearchMapFrom(arg))
                        }
                    },
                    object : Callback<Throwable?> {
                        override fun invoke(arg: Throwable?) {
                            promise.reject(ERR_SEARCH_FAILED, "search request: " + arg?.message)
                        }
                    }
                )
            }
        } else {
            promise.reject(ERR_NO_REQUEST_ARG, "search request: text arg is not provided")
            return
        }
    }

    @ReactMethod
    fun resolveURI(searchQuery: String?, options: ReadableMap?, promise: Promise) {
        if (searchQuery != null) {
            val searchOptions = getSearchOptions(options)
            UiThreadUtil.runOnUiThread {
                getSearchClient().resolveURI(searchQuery, searchOptions,
                    object : Callback<MapSearchItem?> {
                        override fun invoke(arg: MapSearchItem?) {
                            promise.resolve(searchArgsHelper.createSearchMapFrom(arg))
                        }
                    },
                    object : Callback<Throwable?> {
                        override fun invoke(arg: Throwable?) {
                            promise.reject(ERR_SEARCH_FAILED, "search request: " + arg?.message)
                        }
                    }
                )
            }
        } else {
            promise.reject(ERR_NO_REQUEST_ARG, "search request: text arg is not provided")
            return
        }
    }

    @ReactMethod
    fun searchByURI(searchQuery: String?, options: ReadableMap?, promise: Promise) {
        if (searchQuery != null) {
            val searchOptions = getSearchOptions(options)
            UiThreadUtil.runOnUiThread {
                getSearchClient().searchByURI(searchQuery, searchOptions,
                    object : Callback<MapSearchItem?> {
                        override fun invoke(arg: MapSearchItem?) {
                            promise.resolve(searchArgsHelper.createSearchMapFrom(arg))
                        }
                    },
                    object : Callback<Throwable?> {
                        override fun invoke(arg: Throwable?) {
                            promise.reject(ERR_SEARCH_FAILED, "search request: " + arg?.message)
                        }
                    }
                )
            }
        } else {
            promise.reject(ERR_NO_REQUEST_ARG, "search request: text arg is not provided")
            return
        }
    }

    @ReactMethod
    fun searchByPoint(jsPoint: ReadableMap?, zoom: Double?, options: ReadableMap?, promise: Promise) {
        if (jsPoint === null) {
            promise.reject(ERR_NO_REQUEST_ARG, "search request: text arg is not provided")
            return
        }

        val point = PointUtil.readableMapToPoint(jsPoint)
        UiThreadUtil.runOnUiThread {
            getSearchClient().searchPoint(point, (zoom?.toInt() ?: 10), getSearchOptions(options),
                object : Callback<MapSearchItem?> {
                    override fun invoke(arg: MapSearchItem?) {
                        promise.resolve(searchArgsHelper.createSearchMapFrom(arg))
                    }
                },
                object : Callback<Throwable?> {
                    override fun invoke(arg: Throwable?) {
                        promise.reject(ERR_SEARCH_FAILED, "search request: " + arg?.message)
                    }
                }
            )
        }
    }

    @ReactMethod
    fun geoToAddress(jsPoint: ReadableMap?, promise: Promise) {
        if (jsPoint === null) {
            promise.reject(ERR_NO_REQUEST_ARG, "search request: text arg is not provided")
            return
        }

        val point = PointUtil.readableMapToPoint(jsPoint)
        UiThreadUtil.runOnUiThread {
            getSearchClient().searchPoint(point, 10, getSearchOptions(null),
                object : Callback<MapSearchItem?> {
                    override fun invoke(arg: MapSearchItem?) {
                        promise.resolve(searchArgsHelper.createSearchMapFrom(arg))
                    }
                },
                object : Callback<Throwable?> {
                    override fun invoke(arg: Throwable?) {
                        promise.reject(ERR_SEARCH_FAILED, "search request: " + arg?.message)
                    }
                }
            )
        }
    }

    @ReactMethod
    fun addressToGeo(text: String?, promise: Promise) {
        if (text != null) {
            UiThreadUtil.runOnUiThread {
                getSearchClient().searchAddress(text, Geometry.fromPoint(Point(0.0, 0.0)), SearchOptions(),
                    object : Callback<MapSearchItem?> {
                        override fun invoke(arg: MapSearchItem?) {
                            val jsPoint = PointUtil.pointToJsPoint(arg?.point)
                            promise.resolve(jsPoint)
                        }
                    },
                    object : Callback<Throwable?> {
                        override fun invoke(arg: Throwable?) {
                            promise.reject(ERR_SEARCH_FAILED, "search request: " + arg?.message)
                        }
                    }
                )
            }
        } else {
            promise.reject(ERR_NO_REQUEST_ARG, "search request: text arg is not provided")
            return
        }
    }

    private fun getSearchClient(): MapSearchClient {
        if (searchClient == null) {
            searchClient = YandexMapSearchClient()
        }

        return searchClient as MapSearchClient
    }

    companion object {
        private const val ERR_NO_REQUEST_ARG = "YANDEX_SEARCH_ERR_NO_REQUEST_ARG"
        private const val ERR_SEARCH_FAILED = "YANDEX_SEARCH_ERR_SEARCH_FAILED"
    }
}
