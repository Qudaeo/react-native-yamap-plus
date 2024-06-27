package ru.vvdev.yamap;

import android.view.View;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

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
        return new ClusteredYamapView(context);
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
    public void addView(ClusteredYamapView parent, @NonNull View child, int index) {
        super.addView(parent, child, index);
        parent.addFeature(child, index);
    }

    @Override
    public void removeViewAt(ClusteredYamapView parent, int index) {
        parent.removeChild(index);
        super.removeViewAt(parent, index);
    }
}