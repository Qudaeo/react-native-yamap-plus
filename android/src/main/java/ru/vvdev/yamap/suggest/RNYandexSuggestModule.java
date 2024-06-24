package ru.vvdev.yamap.suggest;

import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import java.util.List;

import ru.vvdev.yamap.utils.Callback;

public class RNYandexSuggestModule extends ReactContextBaseJavaModule {
    private static final String ERR_NO_REQUEST_ARG = "YANDEX_SUGGEST_ERR_NO_REQUEST_ARG";
    private static final String ERR_SUGGEST_FAILED = "YANDEX_SUGGEST_ERR_SUGGEST_FAILED";
    private final YandexSuggestRNArgsHelper argsHelper = new YandexSuggestRNArgsHelper();
    @Nullable
    private MapSuggestClient suggestClient;

    public RNYandexSuggestModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @NonNull
    @Override
    public String getName() {
        return "YamapSuggests";
    }

    @ReactMethod
    public void suggest(final String text, final Promise promise) {
        if (text == null) {
            promise.reject(ERR_NO_REQUEST_ARG, "suggest request: text arg is not provided");
            return;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getSuggestClient(getReactApplicationContext()).suggest(text,
                        result -> promise.resolve(argsHelper.createSuggestsMapFrom(result)),
                        e -> promise.reject(ERR_SUGGEST_FAILED, "suggest request: " + e.getMessage())
                );
            }
        });
    }

    @ReactMethod
    public void suggestWithOptions(final String text, final ReadableMap options, final Promise promise) {
        if (text == null) {
            promise.reject(ERR_NO_REQUEST_ARG, "suggest request: text arg is not provided");
            return;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getSuggestClient(getReactApplicationContext()).suggest(text, options,
                        new Callback<List<MapSuggestItem>>() {
                            @Override
                            public void invoke(List<MapSuggestItem> result) {
                                promise.resolve(argsHelper.createSuggestsMapFrom(result));
                            }
                        },
                        new Callback<Throwable>() {
                            @Override
                            public void invoke(Throwable e) {
                                promise.reject(ERR_SUGGEST_FAILED, "suggest request: " + e.getMessage());
                            }
                        }
                );
            }
        });
    }

    @ReactMethod
    void reset() {
        runOnUiThread(() -> getSuggestClient(getReactApplicationContext()).resetSuggest());
    }

    private MapSuggestClient getSuggestClient(Context context) {
        if (suggestClient == null) {
            suggestClient = new YandexMapSuggestClient(context);
        }

        return suggestClient;
    }
}