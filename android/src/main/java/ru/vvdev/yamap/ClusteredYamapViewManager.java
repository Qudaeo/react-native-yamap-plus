package ru.vvdev.yamap;

import android.view.View;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.yandex.mapkit.MapKitFactory;

import ru.vvdev.yamap.view.ClusteredYamapView;

public class ClusteredYamapViewManager extends BaseYamapViewManager<ClusteredYamapView> {
    public static final String REACT_CLASS = "ClusteredYamapView";

    @NonNull
    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected ClusteredYamapView createViewInstanceInternal(@NonNull ThemedReactContext context) {
        var view = new ClusteredYamapView(context);
        MapKitFactory.getInstance().onStart();
        view.onStart();
        return view;
    }

    @Override
    protected ClusteredYamapView castToYamapView(View view) {
        return (ClusteredYamapView) view;
    }

    @ReactProp(name = "clusteredMarkers")
    public void setClusteredMarkers(View view, ReadableArray points) {
        castToYamapView(view).setClusteredMarkers(points.toArrayList());
    }

    @ReactProp(name = "clusterColor")
    public void setClusterColor(View view, int color) {
        castToYamapView(view).setClustersColor(color);
    }

    @Override
    public void addView(ClusteredYamapView parent, View child, int index) {
        parent.addFeature(child, index);
        super.addView(parent, child, index);
    }

    @Override
    public void removeViewAt(ClusteredYamapView parent, int index) {
        parent.removeChild(index);
        super.removeViewAt(parent, index);
    }
}