package ru.vvdev.yamap.search

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import ru.vvdev.yamap.utils.PointUtil

class YandexSearchRNArgsHelper {
    fun createSearchMapFrom(data: MapSearchItem?): WritableMap {
        val result = Arguments.createMap()
        if (data != null) {
            result.putString("formatted", data.formatted)
            result.putString("country_code", data.country_code)
            val components = Arguments.createArray()
            for (i in data.Components!!) {
                val mappedItem = Arguments.createMap()
                mappedItem.putString("kind", i.kind)
                mappedItem.putString("name", i.name)
                components.pushMap(mappedItem);
            }
            result.putArray("Components", components)
            result.putString("uri", data.uri)
            result.putMap("point", PointUtil.pointToJsPoint(data.point))
        }
        return result
    }
}
