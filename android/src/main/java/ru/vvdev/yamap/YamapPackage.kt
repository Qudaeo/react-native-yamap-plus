package ru.vvdev.yamap

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager
import ru.vvdev.yamap.module.SearchModule
import ru.vvdev.yamap.suggest.RNYandexSuggestModule
import ru.vvdev.yamap.view.YamapViewManager
import ru.vvdev.yamap.view.ClusteredYamapViewManager
import ru.vvdev.yamap.view.PolygonViewManager
import ru.vvdev.yamap.view.PolylineViewManager
import ru.vvdev.yamap.view.MarkerViewManager
import ru.vvdev.yamap.view.CircleViewManager

class YamapPackage : ReactPackage {

    override fun createNativeModules(reactContext: ReactApplicationContext) = listOf(
        YamapModule(reactContext),
        RNYandexSuggestModule(reactContext),
        SearchModule(reactContext)
    )

    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
        return listOf(
            YamapViewManager(),
            ClusteredYamapViewManager(),
            PolygonViewManager(),
            PolylineViewManager(),
            MarkerViewManager(),
            CircleViewManager()
        )
    }
}
