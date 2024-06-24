package ru.vvdev.yamap;

import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.runtime.i18n.I18nManagerFactory;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class RNYamapModule extends ReactContextBaseJavaModule implements LifecycleEventListener {
    private static final String REACT_CLASS = "yamap";
    private static ReactApplicationContext reactContext = null;
    private boolean _isInitialized = false;

    RNYamapModule(ReactApplicationContext context) {
        super(context);
        reactContext = context;

        reactContext.addLifecycleEventListener(this);
    }

    private static void emitDeviceEvent(@NonNull String eventName, @Nullable WritableMap eventData) {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, eventData);
    }

    private ReactApplicationContext getContext() {
        return reactContext;
    }

    @NonNull
    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public Map<String, Object> getConstants() {
        return new HashMap<>();
    }

    @ReactMethod
    public void init(final String apiKey, final Promise promise) {
        runOnUiThread(new Thread(() -> {
            Throwable apiKeyException = null;
            try {
                // In case when android application reloads during development
                // MapKitFactory is already initialized
                // And setting api key leads to crash
                try {
                    MapKitFactory.setApiKey(apiKey);
                } catch (Throwable exception) {
                    apiKeyException = exception;
                }

                MapKitFactory.initialize(reactContext);
                _isInitialized = true;
                promise.resolve(null);
            } catch (Exception ex) {
                if (apiKeyException != null) {
                    promise.reject(apiKeyException);
                    return;
                }
                promise.reject(ex);
            }
        }));
    }

    @ReactMethod
    public void setLocale(final String locale, final Promise promise) {
        runOnUiThread(new Thread(() -> {
            try {
                I18nManagerFactory.setLocale(locale);
                promise.resolve(null);
            } catch (Exception ex) {
                promise.reject((ex));
            }
        }));
    }

    @ReactMethod
    public void getLocale(final Promise promise) {
        runOnUiThread(new Thread(() -> {
            try {
                var locale = I18nManagerFactory.getLocale();
                promise.resolve(locale);
            } catch (Exception ex) {
                promise.reject((ex));
            }
        }));
    }

    @ReactMethod
    public void resetLocale(final Promise promise) {
        runOnUiThread(new Thread(() -> {
            try {
                MapKitFactory.setLocale(null);
                promise.resolve(null);
            } catch (Exception ex) {
                promise.reject((ex));
            }
        }));
    }

    @Override
    public void onHostResume() {
        if (!_isInitialized) return;

        MapKitFactory.getInstance().onStart();
    }

    @Override
    public void onHostPause() {
        if (!_isInitialized) return;

        MapKitFactory.getInstance().onStop();
    }

    @Override
    public void onHostDestroy() {
        if (!_isInitialized) return;

        MapKitFactory.getInstance().onStop();
    }
}