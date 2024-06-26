package ru.vvdev.yamap;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;

import java.util.ArrayList;
import java.util.Map;

import ru.vvdev.yamap.view.BaseYamapView;

public abstract class BaseYamapViewManager<T extends BaseYamapView> extends ViewGroupManager<T> {
    public enum Commands {
        SET_CENTER(1),
        FIT_ALL_MARKERS(2),
        FIND_ROUTES(3),
        SET_ZOOM(4),
        GET_CAMERA_POSITION(5),
        GET_VISIBLE_REGION(6),
        SET_TRAFFIC_VISIBLE(7),
        FIT_MARKERS(8),
        GET_SCREEN_POINTS(9),
        GET_WORLD_POINTS(10);

        private final int value;

        Commands(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Commands fromString(String command) {
            switch (command) {
                case "setCenter":
                    return SET_CENTER;
                case "fitAllMarkers":
                    return FIT_ALL_MARKERS;
                case "fitMarkers":
                    return FIT_MARKERS;
                case "findRoutes":
                    return FIND_ROUTES;
                case "setZoom":
                    return SET_ZOOM;
                case "getCameraPosition":
                    return GET_CAMERA_POSITION;
                case "getVisibleRegion":
                    return GET_VISIBLE_REGION;
                case "setTrafficVisible":
                    return SET_TRAFFIC_VISIBLE;
                case "getScreenPoints":
                    return GET_SCREEN_POINTS;
                case "getWorldPoints":
                    return GET_WORLD_POINTS;
                default:
                    throw new IllegalArgumentException(String.format("Unsupported command %s received.", command));
            }
        }
    }

    public static final int LAST_COMMAND_ID = Commands.GET_WORLD_POINTS.getValue();

    @Override
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.<String, Object>builder().build();
    }

    @Override
    public Map getExportedCustomBubblingEventTypeConstants() {
        var v1 = "phasedRegistrationNames";
        var v2 = "bubbled";

        return MapBuilder.builder()
                .put("routes", MapBuilder.of(v1, MapBuilder.of(v2, "onRouteFound")))
                .put("cameraPosition", MapBuilder.of(v1, MapBuilder.of(v2, "onCameraPositionReceived")))
                .put("cameraPositionChanged", MapBuilder.of(v1, MapBuilder.of(v2, "onCameraPositionChanged")))
                .put("visibleRegion", MapBuilder.of(v1, MapBuilder.of(v2, "onVisibleRegionReceived")))
                .put("onMapPress", MapBuilder.of(v1, MapBuilder.of(v2, "onMapPress")))
                .put("onMapLongPress", MapBuilder.of(v1, MapBuilder.of(v2, "onMapLongPress")))
                .put("onMapLoaded", MapBuilder.of(v1, MapBuilder.of(v2, "onMapLoaded")))
                .put("screenToWorldPoints", MapBuilder.of(v1, MapBuilder.of(v2, "onScreenToWorldPointsReceived")))
                .put("worldToScreenPoints", MapBuilder.of(v1, MapBuilder.of(v2, "onWorldToScreenPointsReceived")))
                .build();
    }

    @Override
    public Map<String, Integer> getCommandsMap() {
        Map<String, Integer> map = MapBuilder.newHashMap();
        for (Commands command : Commands.values()) {
            map.put(command.name(), command.getValue());
        }
        return map;
    }

    @Override
    public void receiveCommand(@NonNull T view, String commandType, @Nullable ReadableArray args) {
        var command = Commands.fromString(commandType);
        switch (command) {
            case SET_CENTER:
                if (args != null) {
                    setCenter(view, args.getMap(0), (float) args.getDouble(1), (float) args.getDouble(2), (float) args.getDouble(3), (float) args.getDouble(4), args.getInt(5));
                }
                break;

            case FIT_ALL_MARKERS:
                fitAllMarkers(view);
                break;

            case FIT_MARKERS:
                if (args != null) {
                    fitMarkers(view, args.getArray(0));
                }
                break;

            case FIND_ROUTES:
                if (args != null) {
                    findRoutes(view, args.getArray(0), args.getArray(1), args.getString(2));
                }
                break;

            case SET_ZOOM:
                if (args != null) {
                    view.setZoom((float) args.getDouble(0), (float) args.getDouble(1), args.getInt(2));
                }
                break;

            case GET_CAMERA_POSITION:
                if (args != null) {
                    view.emitCameraPositionToJS(args.getString(0));
                }
                break;

            case GET_VISIBLE_REGION:
                if (args != null) {
                    view.emitVisibleRegionToJS(args.getString(0));
                }
                break;

            case SET_TRAFFIC_VISIBLE:
                if (args != null) {
                    view.setTrafficVisible(args.getBoolean(0));
                }
                break;

            case GET_SCREEN_POINTS:
                if (args != null) {
                    view.emitWorldToScreenPoints(args.getArray(0), args.getString(1));
                }
                break;

            case GET_WORLD_POINTS:
                if (args != null) {
                    view.emitScreenToWorldPoints(args.getArray(0), args.getString(1));
                }
                break;

            default:
                throw new IllegalArgumentException(String.format("Unsupported command %s received by %s.", commandType, getClass().getSimpleName()));
        }
    }

    protected abstract T createViewInstanceInternal(@NonNull ThemedReactContext context);

    @NonNull
    @Override
    public T createViewInstance(@NonNull ThemedReactContext context) {
        return createViewInstanceInternal(context);
    }

    @ReactProp(name = "userLocationIcon")
    public void setUserLocationIcon(View view, String icon) {
        if (icon == null) return;

        castToYamapView(view).setUserLocationIcon(icon);
    }

    @ReactProp(name = "userLocationIconScale")
    public void setUserLocationIconScale(View view, float scale) {
        castToYamapView(view).setUserLocationIconScale(scale);
    }

    @ReactProp(name = "userLocationAccuracyFillColor")
    public void setUserLocationAccuracyFillColor(View view, int color) {
        castToYamapView(view).setUserLocationAccuracyFillColor(color);
    }

    @ReactProp(name = "userLocationAccuracyStrokeColor")
    public void setUserLocationAccuracyStrokeColor(View view, int color) {
        castToYamapView(view).setUserLocationAccuracyStrokeColor(color);
    }

    @ReactProp(name = "userLocationAccuracyStrokeWidth")
    public void setUserLocationAccuracyStrokeWidth(View view, float width) {
        castToYamapView(view).setUserLocationAccuracyStrokeWidth(width);
    }

    @ReactProp(name = "showUserPosition")
    public void setShowUserPosition(View view, Boolean show) {
        castToYamapView(view).setShowUserPosition(show);
    }

    @ReactProp(name = "followUser")
    public void setFollowUser(View view, Boolean follow) {
        castToYamapView(view).setFollowUser(follow);
    }

    @ReactProp(name = "nightMode")
    public void setNightMode(View view, boolean nightMode) {
        castToYamapView(view).setNightMode(nightMode);
    }

    @ReactProp(name = "scrollGesturesEnabled")
    public void setScrollGesturesEnabled(View view, Boolean scrollGesturesEnabled) {
        castToYamapView(view).setScrollGesturesEnabled(scrollGesturesEnabled);
    }

    @ReactProp(name = "zoomGesturesEnabled")
    public void setZoomGesturesEnabled(View view, Boolean zoomGesturesEnabled) {
        castToYamapView(view).setZoomGesturesEnabled(zoomGesturesEnabled);
    }

    @ReactProp(name = "tiltGesturesEnabled")
    public void setTiltGesturesEnabled(View view, Boolean tiltGesturesEnabled) {
        castToYamapView(view).setTiltGesturesEnabled(tiltGesturesEnabled);
    }

    @ReactProp(name = "fastTapEnabled")
    public void setFastTapEnabled(View view, Boolean fastTapEnabled) {
        castToYamapView(view).setFastTapEnabled(fastTapEnabled);
    }

    @ReactProp(name = "mapStyle")
    public void setMapStyle(View view, String style) {
        if (style == null) return;
        castToYamapView(view).setMapStyle(style);
    }

    @ReactProp(name = "mapType")
    public void setMapType(View view, String type) {
        if (type == null) return;
        castToYamapView(view).setMapType(type);
    }

    @ReactProp(name = "initialRegion")
    public void setInitialRegion(View view, ReadableMap params) {
        if (params == null) return;
        castToYamapView(view).setInitialRegion(params);
    }

    @ReactProp(name = "interactive")
    public void setInteractive(View view, boolean interactive) {
        castToYamapView(view).setInteractive(interactive);
    }

    @ReactProp(name = "logoPosition")
    public void setLogoPosition(View view, ReadableMap params) {
        if (params == null) return;
        castToYamapView(view).setLogoPosition(params);
    }

    @ReactProp(name = "logoPadding")
    public void setLogoPadding(View view, ReadableMap params) {
        if (params == null) return;
        castToYamapView(view).setLogoPadding(params);
    }

    protected abstract T castToYamapView(View view);

    protected void setCenter(T view, ReadableMap center, float zoom, float azimuth, float tilt, float duration, int animation) {
        if (center == null) return;

        var centerPosition = new Point(center.getDouble("lat"), center.getDouble("lon"));
        var pos = new CameraPosition(centerPosition, zoom, azimuth, tilt);
        view.setCenter(pos, duration, animation);
    }

    protected void fitAllMarkers(T view) {
        view.fitAllMarkers();
    }

    protected void fitMarkers(T view, ReadableArray jsPoints) {
        if (jsPoints == null) return;

        var points = new ArrayList<Point>();

        for (var i = 0; i < jsPoints.size(); ++i) {
            var point = jsPoints.getMap(i);
            points.add(new Point(point.getDouble("lat"), point.getDouble("lon")));
        }

        view.fitMarkers(points);
    }

    protected void findRoutes(T view, ReadableArray jsPoints, ReadableArray jsVehicles, String id) {
        if (jsPoints == null) return;

        var points = new ArrayList<Point>();
        for (var i = 0; i < jsPoints.size(); ++i) {
            var point = jsPoints.getMap(i);
            points.add(new Point(point.getDouble("lat"), point.getDouble("lon")));
        }
        var vehicles = new ArrayList<String>();
        if (jsVehicles != null) {
            for (var i = 0; i < jsVehicles.size(); ++i) {
                vehicles.add(jsVehicles.getString(i));
            }
        }

        view.findRoutes(points, vehicles, id);
    }
}