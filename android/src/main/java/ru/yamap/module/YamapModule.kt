package ru.yamap.module

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.UiThreadUtil
import com.yandex.mapkit.MapKitFactory
import com.yandex.runtime.i18n.I18nManagerFactory
import ru.yamap.NativeYamapModuleSpec

class YamapModule(reactContext: ReactApplicationContext) : NativeYamapModuleSpec(reactContext) {

    override fun getName() = NAME

    override fun init(apiKey: String?, promise: Promise) {
        UiThreadUtil.runOnUiThread(Thread(Runnable {
            var apiKeyException: Throwable? = null
            try {
                // In case when android application reloads during development
                // MapKitFactory is already initialized
                // And setting api key leads to crash
                try {
                    MapKitFactory.setApiKey(apiKey!!)
                } catch (exception: Throwable) {
                    apiKeyException = exception
                }

                MapKitFactory.initialize(reactApplicationContext)
                MapKitFactory.getInstance().onStart()
                promise.resolve(null)
            } catch (exception: Exception) {
                if (apiKeyException != null) {
                    promise.reject(apiKeyException)
                    return@Runnable
                }
                promise.reject(exception)
            }
        }))
    }

    override fun setLocale(locale: String?, promise: Promise) {
        UiThreadUtil.runOnUiThread(Thread {
            try {
                MapKitFactory.setLocale(locale)
                promise.resolve(null)
            } catch (e: Throwable) {
                promise.reject("101", e.message)
            }
        })
    }

    override fun getLocale(promise: Promise) {
        UiThreadUtil.runOnUiThread(Thread {
            try {
                val locale = I18nManagerFactory.getLocale()
                promise.resolve(locale)
            } catch (e: Throwable) {
                promise.reject("102", e.message)
            }
        })
    }

    override fun resetLocale(promise: Promise) {
        UiThreadUtil.runOnUiThread(Thread {
            try {
                I18nManagerFactory.setLocale(null)
                promise.resolve(null)
            } catch (e: Throwable) {
                promise.reject("103", e.message)
            }
        })
    }

    companion object {
        const val NAME = "RTNYamapModule"
    }
}
