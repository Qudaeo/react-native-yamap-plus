package ru.vvdev.yamap;

import android.view.View;

import androidx.annotation.NonNull;

import com.facebook.react.uimanager.ThemedReactContext;
import com.yandex.mapkit.MapKitFactory;

import ru.vvdev.yamap.view.YamapView;

public class YamapViewManager extends BaseYamapViewManager<YamapView> {
    public static final String REACT_CLASS = "YamapView";

    @NonNull
    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected YamapView createViewInstanceInternal(@NonNull ThemedReactContext context) {
        return new YamapView(context);
    }

    @Override
    protected YamapView castToYamapView(View view) {
        return (YamapView) view;
    }
}