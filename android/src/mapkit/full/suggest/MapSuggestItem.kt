package ru.yamap.suggest

import com.yandex.mapkit.geometry.Point

class MapSuggestItem {
    @JvmField
    var searchText: String? = null
    @JvmField
    var title: String? = null
    @JvmField
    var subtitle: String? = null
    @JvmField
    var uri: String? = null
    @JvmField
    var center: Point? = null
}
