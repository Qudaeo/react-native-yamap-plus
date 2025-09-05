package ru.yamap

import com.facebook.react.BaseReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.model.ReactModuleInfo
import com.facebook.react.module.model.ReactModuleInfoProvider
import com.facebook.react.uimanager.ViewManager
import ru.yamap.module.SearchModule
import ru.yamap.module.SearchModuleImpl
import ru.yamap.module.SuggestsModule
import ru.yamap.module.SuggestsModuleImpl
import ru.yamap.module.TransportModule
import ru.yamap.module.TransportModuleImpl
import ru.yamap.module.YamapModule
import ru.yamap.module.YamapModuleImpl
import ru.yamap.view.YamapViewManager
import ru.yamap.view.ClusteredYamapViewManager
import ru.yamap.view.PolygonViewManager
import ru.yamap.view.PolylineViewManager
import ru.yamap.view.MarkerViewManager
import ru.yamap.view.CircleViewManager

class YamapPackage : BaseReactPackage() {

    override fun getModule(
        name: String,
        reactContext: ReactApplicationContext
    ): NativeModule? {
        return when (name) {
            TransportModuleImpl.NAME -> TransportModule(reactContext)
            SearchModuleImpl.NAME -> SearchModule(reactContext)
            SuggestsModuleImpl.NAME -> SuggestsModule(reactContext)
            YamapModuleImpl.NAME -> YamapModule(reactContext)
            else -> null
        }
    }

    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> =
        listOf(
            YamapViewManager(),
            ClusteredYamapViewManager(),
            PolygonViewManager(),
            PolylineViewManager(),
            MarkerViewManager(),
            CircleViewManager()
        )

    override fun getReactModuleInfoProvider(): ReactModuleInfoProvider =
        ReactModuleInfoProvider {
            hashMapOf(
                TransportModuleImpl.NAME to ReactModuleInfo(
                    TransportModuleImpl.NAME,
                    TransportModuleImpl.NAME,
                    canOverrideExistingModule = false,
                    needsEagerInit = false,
                    isCxxModule = false,
                    isTurboModule = true
                ),
                SearchModuleImpl.NAME to ReactModuleInfo(
                    SearchModuleImpl.NAME,
                    SearchModuleImpl.NAME,
                    canOverrideExistingModule = false,
                    needsEagerInit = false,
                    isCxxModule = false,
                    isTurboModule = true
                ),
                SuggestsModuleImpl.NAME to ReactModuleInfo(
                    SuggestsModuleImpl.NAME,
                    SuggestsModuleImpl.NAME,
                    canOverrideExistingModule = false,
                    needsEagerInit = false,
                    isCxxModule = false,
                    isTurboModule = true
                ),
                YamapModuleImpl.NAME to ReactModuleInfo(
                    YamapModuleImpl.NAME,
                    YamapModuleImpl.NAME,
                    canOverrideExistingModule = false,
                    needsEagerInit = false,
                    isCxxModule = false,
                    isTurboModule = true
                ),
            )
        }
}
