package ru.vvdev.yamap.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
import com.yandex.mapkit.ScreenPoint;
import com.yandex.mapkit.directions.DirectionsFactory;
import com.yandex.mapkit.directions.driving.DrivingOptions;
import com.yandex.mapkit.directions.driving.DrivingRoute;
import com.yandex.mapkit.directions.driving.DrivingRouter;
import com.yandex.mapkit.directions.driving.DrivingRouterType;
import com.yandex.mapkit.directions.driving.DrivingSection;
import com.yandex.mapkit.directions.driving.DrivingSession;
import com.yandex.mapkit.directions.driving.VehicleOptions;
import com.yandex.mapkit.geometry.Geometry;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polyline;
import com.yandex.mapkit.geometry.SubpolylineHelper;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.logo.Alignment;
import com.yandex.mapkit.logo.HorizontalAlignment;
import com.yandex.mapkit.logo.Padding;
import com.yandex.mapkit.logo.VerticalAlignment;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateReason;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapLoadStatistics;
import com.yandex.mapkit.map.MapLoadedListener;
import com.yandex.mapkit.map.MapType;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.VisibleRegion;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.traffic.TrafficLayer;
import com.yandex.mapkit.traffic.TrafficLevel;
import com.yandex.mapkit.traffic.TrafficListener;
import com.yandex.mapkit.transport.TransportFactory;
import com.yandex.mapkit.transport.masstransit.FilterVehicleTypes;
import com.yandex.mapkit.transport.masstransit.MasstransitRouter;
import com.yandex.mapkit.transport.masstransit.PedestrianRouter;
import com.yandex.mapkit.transport.masstransit.Route;
import com.yandex.mapkit.transport.masstransit.Section;
import com.yandex.mapkit.transport.masstransit.Session;
import com.yandex.mapkit.transport.masstransit.TimeOptions;
import com.yandex.mapkit.transport.masstransit.TransitOptions;
import com.yandex.mapkit.transport.masstransit.Transport;
import com.yandex.mapkit.transport.masstransit.Weight;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import ru.vvdev.yamap.models.ReactMapObject;
import ru.vvdev.yamap.utils.ImageLoader;
import ru.vvdev.yamap.utils.RouteManager;

public abstract class BaseYamapView extends MapView implements UserLocationObjectListener, CameraListener, InputListener, TrafficListener, MapLoadedListener, View.OnAttachStateChangeListener {
    static private final HashMap<String, ImageProvider> _icons = new HashMap<>();

    private final Map _map;
    private String _userLocationIcon = "";
    private float _userLocationIconScale = 1.f;
    private Bitmap _userLocationBitmap = null;
    private final RouteManager _routeManager = new RouteManager();
    private final MasstransitRouter _masstransitRouter = TransportFactory.getInstance().createMasstransitRouter();
    private final DrivingRouter _drivingRouter;
    private final PedestrianRouter _pedestrianRouter = TransportFactory.getInstance().createPedestrianRouter();
    private UserLocationLayer _userLocationLayer = null;
    private int _userLocationAccuracyFillColor = 0;
    private int _userLocationAccuracyStrokeColor = 0;
    private float _userLocationAccuracyStrokeWidth = 0.f;
    private TrafficLayer _trafficLayer = null;
    private UserLocationView _userLocationView = null;

    public BaseYamapView(Context context) {
        super(context);

        _drivingRouter = DirectionsFactory.getInstance().createDrivingRouter(DrivingRouterType.COMBINED);

        _map = getMapWindow().getMap();
        _map.addCameraListener(this);
        _map.addInputListener(this);
        _map.setMapLoadedListener(this);

        addOnAttachStateChangeListener(this);
    }

    public void setImage(final String iconSource, final PlacemarkMapObject mapObject, final IconStyle iconStyle) {
        if (mapObject == null) return;

        if (_icons.get(iconSource) == null) {
            ImageLoader.DownloadImageBitmap(getContext(), iconSource, bitmap -> {
                try {
                    var icon = ImageProvider.fromBitmap(bitmap);
                    _icons.put(iconSource, icon);

                    mapObject.setIcon(icon);
                    mapObject.setIconStyle(iconStyle);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            mapObject.setIcon(Objects.requireNonNull(_icons.get(iconSource)));
            mapObject.setIconStyle(iconStyle);
        }
    }

    public void setCenter(CameraPosition position, float duration, int animation) {
        try {
            if (duration > 0) {
                var anim = animation == 0 ? Animation.Type.SMOOTH : Animation.Type.LINEAR;

                _map.move(position, new Animation(anim, duration), null);
            } else {
                _map.move(position);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private WritableMap positionToJSON(CameraPosition position, CameraUpdateReason reason, boolean finished) {
        var cameraPosition = Arguments.createMap();

        var point = position.getTarget();

        var target = Arguments.createMap();
        target.putDouble("lat", point.getLatitude());
        target.putDouble("lon", point.getLongitude());

        cameraPosition.putMap("point", target);
        cameraPosition.putDouble("azimuth", position.getAzimuth());
        cameraPosition.putDouble("tilt", position.getTilt());
        cameraPosition.putDouble("zoom", position.getZoom());
        cameraPosition.putString("reason", reason.toString());
        cameraPosition.putBoolean("finished", finished);

        return cameraPosition;
    }

    private WritableMap screenPointToJSON(ScreenPoint screenPoint) {
        var result = Arguments.createMap();

        result.putDouble("x", screenPoint.getX());
        result.putDouble("y", screenPoint.getY());

        return result;
    }

    private WritableMap worldPointToJSON(Point worldPoint) {
        var result = Arguments.createMap();

        result.putDouble("lat", worldPoint.getLatitude());
        result.putDouble("lon", worldPoint.getLongitude());

        return result;
    }

    private WritableMap visibleRegionToJSON(VisibleRegion region) {
        var result = Arguments.createMap();

        var bl = Arguments.createMap();
        bl.putDouble("lat", region.getBottomLeft().getLatitude());
        bl.putDouble("lon", region.getBottomLeft().getLongitude());
        result.putMap("bottomLeft", bl);

        var br = Arguments.createMap();
        br.putDouble("lat", region.getBottomRight().getLatitude());
        br.putDouble("lon", region.getBottomRight().getLongitude());
        result.putMap("bottomRight", br);

        var tl = Arguments.createMap();
        tl.putDouble("lat", region.getTopLeft().getLatitude());
        tl.putDouble("lon", region.getTopLeft().getLongitude());
        result.putMap("topLeft", tl);

        var tr = Arguments.createMap();
        tr.putDouble("lat", region.getTopRight().getLatitude());
        tr.putDouble("lon", region.getTopRight().getLongitude());
        result.putMap("topRight", tr);

        return result;
    }

    public void emitCameraPositionToJS(String id) {
        var position = _map.getCameraPosition();
        var result = positionToJSON(position, CameraUpdateReason.valueOf("APPLICATION"), true);
        result.putString("id", id);

        var reactContext = (ReactContext) getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "cameraPosition", result);
    }

    public void emitVisibleRegionToJS(String id) {
        var visibleRegion = _map.getVisibleRegion();
        var result = visibleRegionToJSON(visibleRegion);
        result.putString("id", id);

        var reactContext = (ReactContext) getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "visibleRegion", result);
    }

    public void emitWorldToScreenPoints(ReadableArray worldPoints, String id) {
        var screenPoints = Arguments.createArray();

        for (var i = 0; i < worldPoints.size(); ++i) {
            var p = worldPoints.getMap(i);
            var worldPoint = new Point(p.getDouble("lat"), p.getDouble("lon"));
            var screenPoint = getMapWindow().worldToScreen(worldPoint);
            if (screenPoint != null) {
                screenPoints.pushMap(screenPointToJSON(screenPoint));
            }
        }

        var result = Arguments.createMap();
        result.putString("id", id);
        result.putArray("screenPoints", screenPoints);

        var reactContext = (ReactContext) getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "worldToScreenPoints", result);
    }

    public void emitScreenToWorldPoints(ReadableArray screenPoints, String id) {
        var worldPoints = Arguments.createArray();

        for (var i = 0; i < screenPoints.size(); ++i) {
            var p = screenPoints.getMap(i);
            var screenPoint = new ScreenPoint((float) p.getDouble("x"), (float) p.getDouble("y"));
            var worldPoint = getMapWindow().screenToWorld(screenPoint);
            if (worldPoint != null) {
                worldPoints.pushMap(worldPointToJSON(worldPoint));
            }
        }

        var result = Arguments.createMap();
        result.putString("id", id);
        result.putArray("worldPoints", worldPoints);

        var reactContext = (ReactContext) getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "screenToWorldPoints", result);
    }

    public void setZoom(Float zoom, float duration, int animation) {
        var prevPosition = _map.getCameraPosition();
        var position = new CameraPosition(prevPosition.getTarget(), zoom, prevPosition.getAzimuth(), prevPosition.getTilt());
        setCenter(position, duration, animation);
    }

    public void findRoutes(ArrayList<Point> points, final ArrayList<String> vehicles, final String id) {
        final var self = this;
        if (vehicles.size() == 1 && vehicles.get(0).equals("car")) {
            var listener = new DrivingSession.DrivingRouteListener() {
                @Override
                public void onDrivingRoutes(@NonNull List<DrivingRoute> routes) {
                    var jsonRoutes = Arguments.createArray();
                    for (var i = 0; i < routes.size(); ++i) {
                        var route = routes.get(i);
                        var jsonRoute = Arguments.createMap();
                        var id = RouteManager.generateId();

                        jsonRoute.putString("id", id);
                        var sections = Arguments.createArray();
                        for (var section : route.getSections()) {
                            var jsonSection = convertDrivingRouteSection(route, section, i);
                            sections.pushMap(jsonSection);
                        }

                        jsonRoute.putArray("sections", sections);
                        jsonRoutes.pushMap(jsonRoute);
                    }
                    self.onRoutesFound(id, jsonRoutes, "success");
                }

                @Override
                public void onDrivingRoutesError(@NonNull Error error) {
                    self.onRoutesFound(id, Arguments.createArray(), "error");
                }
            };

            var requestPoints = new ArrayList<RequestPoint>(points.size());
            for (var i = 0; i < points.size(); ++i) {
                var point = points.get(i);
                var requestPoint = new RequestPoint(point, RequestPointType.WAYPOINT, null, null);
                requestPoints.add(requestPoint);
            }
            _drivingRouter.requestRoutes(requestPoints, new DrivingOptions(), new VehicleOptions(), listener);
            return;
        }

        var requestPoints = new ArrayList<RequestPoint>();
        for (var i = 0; i < points.size(); ++i) {
            var point = points.get(i);
            requestPoints.add(new RequestPoint(point, RequestPointType.WAYPOINT, null, null));
        }

        var listener = new Session.RouteListener() {
            @Override
            public void onMasstransitRoutes(@NonNull List<Route> routes) {
                var jsonRoutes = Arguments.createArray();
                for (var i = 0; i < routes.size(); ++i) {
                    var route = routes.get(i);
                    var jsonRoute = Arguments.createMap();
                    var id = RouteManager.generateId();
                    self._routeManager.saveRoute(route, id);
                    jsonRoute.putString("id", id);
                    var sections = Arguments.createArray();
                    for (var section : route.getSections()) {
                        var jsonSection = convertRouteSection(route, section, SubpolylineHelper.subpolyline(route.getGeometry(), section.getGeometry()), route.getMetadata().getWeight(), i);
                        sections.pushMap(jsonSection);
                    }
                    jsonRoute.putArray("sections", sections);
                    jsonRoutes.pushMap(jsonRoute);
                }
                self.onRoutesFound(id, jsonRoutes, "success");
            }

            @Override
            public void onMasstransitRoutesError(@NonNull Error error) {
                self.onRoutesFound(id, Arguments.createArray(), "error");
            }
        };

        if (vehicles.isEmpty()) {
            _pedestrianRouter.requestRoutes(requestPoints, new TimeOptions(), false, listener);
            return;
        }

        var transitOptions = new TransitOptions(FilterVehicleTypes.NONE.value, new TimeOptions());
        _masstransitRouter.requestRoutes(requestPoints, transitOptions, false, listener);
    }

    public void fitAllMarkers() {
        var size = getChildCount();
        var points = new ArrayList<Point>(size);
        for (var i = 0; i < size; ++i) {
            var obj = getChildAt(i);
            if (obj instanceof final YamapMarker marker) {
                points.add(marker.getPoint());
            }
        }

        fitMarkers(points);
    }

    public void fitMarkers(ArrayList<Point> points) {
        if (points.isEmpty()) return;

        if (points.size() == 1) {
            var center = new Point(points.get(0).getLatitude(), points.get(0).getLongitude());
            _map.move(new CameraPosition(center, 15, 0, 0));
            return;
        }

        var polyline = new Polyline(points);
        var geometry = Geometry.fromPolyline(polyline);
        var cameraPosition = _map.cameraPosition(geometry);
        cameraPosition = new CameraPosition(cameraPosition.getTarget(), cameraPosition.getZoom() - 0.8f, cameraPosition.getAzimuth(), cameraPosition.getTilt());
        _map.move(cameraPosition, new Animation(Animation.Type.SMOOTH, 0.7f), null);
    }

    // PROPS
    public void setUserLocationIcon(final String iconSource) {
        if (Objects.equals(_userLocationIcon, iconSource)) return;

        // todo[0]: можно устанавливать разные иконки на покой и движение. Дополнительно можно устанавливать стиль иконки, например scale
        _userLocationIcon = iconSource;
        ImageLoader.DownloadImageBitmap(getContext(), iconSource, bitmap -> {
            if (iconSource.equals(_userLocationIcon)) {
                _userLocationBitmap = bitmap;
                updateUserLocationIcon();
            }
        });
    }

    public void setUserLocationIconScale(float scale) {
        if (_userLocationIconScale == scale) return;
        _userLocationIconScale = scale;

        updateUserLocationIcon();
    }

    public void setUserLocationAccuracyFillColor(int color) {
        if (_userLocationAccuracyFillColor == color) return;
        _userLocationAccuracyFillColor = color;

        updateUserLocationIcon();
    }

    public void setUserLocationAccuracyStrokeColor(int color) {
        if (_userLocationAccuracyStrokeColor == color) return;
        _userLocationAccuracyStrokeColor = color;

        updateUserLocationIcon();
    }

    public void setUserLocationAccuracyStrokeWidth(float width) {
        if (_userLocationAccuracyStrokeWidth == width) return;
        _userLocationAccuracyStrokeWidth = width;

        updateUserLocationIcon();
    }

    public void setMapStyle(@Nullable String style) {
        if (style == null) return;

        _map.setMapStyle(style);
    }

    public void setMapType(@Nullable String type) {
        if (type != null) {
            switch (type) {
                case "none":
                    _map.setMapType(MapType.NONE);
                    break;

                case "raster":
                    _map.setMapType(MapType.MAP);
                    break;

                default:
                    _map.setMapType(MapType.VECTOR_MAP);
                    break;
            }
        }
    }

    public void setInitialRegion(@Nullable ReadableMap params) {
        if (params == null || (!params.hasKey("lat") || params.isNull("lat")) || (!params.hasKey("lon") && params.isNull("lon")))
            return;

        var initialRegionZoom = 10.f;
        var initialRegionAzimuth = 0.f;
        var initialRegionTilt = 0.f;

        if (params.hasKey("zoom") && !params.isNull("zoom"))
            initialRegionZoom = (float) params.getDouble("zoom");

        if (params.hasKey("azimuth") && !params.isNull("azimuth"))
            initialRegionAzimuth = (float) params.getDouble("azimuth");

        if (params.hasKey("tilt") && !params.isNull("tilt"))
            initialRegionTilt = (float) params.getDouble("tilt");

        var initialPosition = new Point(params.getDouble("lat"), params.getDouble("lon"));
        var initialCameraPosition = new CameraPosition(initialPosition, initialRegionZoom, initialRegionAzimuth, initialRegionTilt);
        setCenter(initialCameraPosition, 0.f, 0);
    }

    public void setLogoPosition(@Nullable ReadableMap params) {
        if (params == null) return;

        var horizontalAlignment = HorizontalAlignment.RIGHT;
        var verticalAlignment = VerticalAlignment.BOTTOM;

        if (params.hasKey("horizontal") && !params.isNull("horizontal")) {
            switch (Objects.requireNonNull(params.getString("horizontal"))) {
                case "left":
                    horizontalAlignment = HorizontalAlignment.LEFT;
                    break;

                case "center":
                    horizontalAlignment = HorizontalAlignment.CENTER;
                    break;

                default:
                    break;
            }
        }

        if (params.hasKey("vertical") && !params.isNull("vertical")) {
            switch (Objects.requireNonNull(params.getString("vertical"))) {
                case "top":
                    verticalAlignment = VerticalAlignment.TOP;
                    break;

                default:
                    break;
            }
        }

        _map.getLogo().setAlignment(new Alignment(horizontalAlignment, verticalAlignment));
    }

    public void setLogoPadding(@Nullable ReadableMap params) {
        if (params == null) return;

        var horizontalPadding = (params.hasKey("horizontal") && !params.isNull("horizontal")) ? params.getInt("horizontal") : 0;
        var verticalPadding = (params.hasKey("vertical") && !params.isNull("vertical")) ? params.getInt("vertical") : 0;
        _map.getLogo().setPadding(new Padding(horizontalPadding, verticalPadding));
    }

    public void setInteractive(boolean interactive) {
        setNoninteractive(!interactive);
    }

    public void setNightMode(Boolean nightMode) {
        _map.setNightModeEnabled(nightMode);
    }

    public void setScrollGesturesEnabled(Boolean scrollGesturesEnabled) {
        _map.setScrollGesturesEnabled(scrollGesturesEnabled);
    }

    public void setZoomGesturesEnabled(Boolean zoomGesturesEnabled) {
        _map.setZoomGesturesEnabled(zoomGesturesEnabled);
    }

    public void setRotateGesturesEnabled(Boolean rotateGesturesEnabled) {
        _map.setRotateGesturesEnabled(rotateGesturesEnabled);
    }

    public void setFastTapEnabled(Boolean fastTapEnabled) {
        _map.setFastTapEnabled(fastTapEnabled);
    }

    public void setTiltGesturesEnabled(Boolean tiltGesturesEnabled) {
        _map.setTiltGesturesEnabled(tiltGesturesEnabled);
    }

    public void setTrafficVisible(Boolean isVisible) {
        if (_trafficLayer == null) {
            _trafficLayer = MapKitFactory.getInstance().createTrafficLayer(getMapWindow());
        }

        if (isVisible) {
            _trafficLayer.addTrafficListener(this);
            _trafficLayer.setTrafficVisible(true);
        } else {
            _trafficLayer.removeTrafficListener(this);
            _trafficLayer.setTrafficVisible(false);
        }
    }

    public void setShowUserPosition(Boolean show) {
        if (_userLocationLayer == null) {
            _userLocationLayer = MapKitFactory.getInstance().createUserLocationLayer(getMapWindow());
        }

        if (show) {
            _userLocationLayer.setObjectListener(this);
            _userLocationLayer.setVisible(true);
            _userLocationLayer.setHeadingEnabled(true);
        } else {
            _userLocationLayer.setVisible(false);
            _userLocationLayer.setHeadingEnabled(false);
            _userLocationLayer.setObjectListener(null);
        }
    }

    public void setFollowUser(Boolean follow) {
        if (_userLocationLayer == null) {
            setShowUserPosition(true);
        }

        if (follow) {
            _userLocationLayer.setAutoZoomEnabled(true);
            _userLocationLayer.setAnchor(new PointF((float) (getWidth() * 0.5), (float) (getHeight() * 0.5)), new PointF((float) (getWidth() * 0.5), (float) (getHeight() * 0.83)));
        } else {
            _userLocationLayer.setAutoZoomEnabled(false);
            _userLocationLayer.resetAnchor();
        }
    }

    private WritableMap convertRouteSection(Route route, final Section section, Polyline geometry, Weight routeWeight, int routeIndex) {
        var data = section.getMetadata().getData();
        var routeMetadata = Arguments.createMap();
        var routeWeightData = Arguments.createMap();
        var sectionWeightData = Arguments.createMap();
        var transports = new HashMap<String, ArrayList<String>>();
        routeWeightData.putString("time", routeWeight.getTime().getText());
        routeWeightData.putInt("transferCount", routeWeight.getTransfersCount());
        routeWeightData.putDouble("walkingDistance", routeWeight.getWalkingDistance().getValue());
        sectionWeightData.putString("time", section.getMetadata().getWeight().getTime().getText());
        sectionWeightData.putInt("transferCount", section.getMetadata().getWeight().getTransfersCount());
        sectionWeightData.putDouble("walkingDistance", section.getMetadata().getWeight().getWalkingDistance().getValue());
        routeMetadata.putMap("sectionInfo", sectionWeightData);
        routeMetadata.putMap("routeInfo", routeWeightData);
        routeMetadata.putInt("routeIndex", routeIndex);

        final WritableArray stops = new WritableNativeArray();
        for (var stop : section.getStops()) {
            stops.pushString(stop.getMetadata().getStop().getName());
        }

        routeMetadata.putArray("stops", stops);

        if (data.getTransports() != null) {
            for (var transport : data.getTransports()) {
                for (var type : transport.getLine().getVehicleTypes()) {
                    if (type.equals("suburban")) continue;
                    if (transports.get(type) != null) {
                        var list = transports.get(type);
                        if (list != null) {
                            list.add(transport.getLine().getName());
                            transports.put(type, list);
                        }
                    } else {
                        var list = new ArrayList<String>();
                        list.add(transport.getLine().getName());
                        transports.put(type, list);
                    }
                    routeMetadata.putString("type", type);
                    var color = Color.BLACK;
                    if (transportHasStyle(transport)) {
                        try {
                            var style = transport.getLine().getStyle();
                            if (style != null) {
                                var styleColor = style.getColor();
                                if (styleColor != null) color = styleColor;
                            }
                        } catch (Exception ignored) {
                        }
                    }
                    routeMetadata.putString("sectionColor", formatColor(color));
                }
            }
        } else {
            routeMetadata.putString("sectionColor", formatColor(Color.DKGRAY));
            if (section.getMetadata().getWeight().getWalkingDistance().getValue() == 0) {
                routeMetadata.putString("type", "waiting");
            } else {
                routeMetadata.putString("type", "walk");
            }
        }

        var wTransports = Arguments.createMap();

        for (var entry : transports.entrySet()) {
            wTransports.putArray(entry.getKey(), Arguments.fromList(entry.getValue()));
        }

        routeMetadata.putMap("transports", wTransports);
        var subpolyline = SubpolylineHelper.subpolyline(route.getGeometry(), section.getGeometry());
        var linePoints = subpolyline.getPoints();
        var jsonPoints = Arguments.createArray();

        for (var point : linePoints) {
            var jsonPoint = Arguments.createMap();
            jsonPoint.putDouble("lat", point.getLatitude());
            jsonPoint.putDouble("lon", point.getLongitude());
            jsonPoints.pushMap(jsonPoint);
        }

        routeMetadata.putArray("points", jsonPoints);

        return routeMetadata;
    }

    private WritableMap convertDrivingRouteSection(DrivingRoute route, final DrivingSection section, int routeIndex) {
        var routeWeight = route.getMetadata().getWeight();
        var routeMetadata = Arguments.createMap();
        var routeWeightData = Arguments.createMap();
        var sectionWeightData = Arguments.createMap();

        // var transports = new HashMap<String, ArrayList<String>>();

        routeWeightData.putString("time", routeWeight.getTime().getText());
        routeWeightData.putString("timeWithTraffic", routeWeight.getTimeWithTraffic().getText());
        routeWeightData.putDouble("distance", routeWeight.getDistance().getValue());
        sectionWeightData.putString("time", section.getMetadata().getWeight().getTime().getText());
        sectionWeightData.putString("timeWithTraffic", section.getMetadata().getWeight().getTimeWithTraffic().getText());
        sectionWeightData.putDouble("distance", section.getMetadata().getWeight().getDistance().getValue());
        routeMetadata.putMap("sectionInfo", sectionWeightData);
        routeMetadata.putMap("routeInfo", routeWeightData);
        routeMetadata.putInt("routeIndex", routeIndex);

        final WritableArray stops = new WritableNativeArray();
        routeMetadata.putArray("stops", stops);
        routeMetadata.putString("sectionColor", formatColor(Color.DKGRAY));

        if (section.getMetadata().getWeight().getDistance().getValue() == 0) {
            routeMetadata.putString("type", "waiting");
        } else {
            routeMetadata.putString("type", "car");
        }

        var wTransports = Arguments.createMap();
        routeMetadata.putMap("transports", wTransports);
        var subpolyline = SubpolylineHelper.subpolyline(route.getGeometry(), section.getGeometry());
        var linePoints = subpolyline.getPoints();
        var jsonPoints = Arguments.createArray();

        for (var point : linePoints) {
            var jsonPoint = Arguments.createMap();
            jsonPoint.putDouble("lat", point.getLatitude());
            jsonPoint.putDouble("lon", point.getLongitude());
            jsonPoints.pushMap(jsonPoint);
        }

        routeMetadata.putArray("points", jsonPoints);

        return routeMetadata;
    }

    public void onRoutesFound(String id, WritableArray routes, String status) {
        var event = Arguments.createMap();
        event.putArray("routes", routes);
        event.putString("id", id);
        event.putString("status", status);

        var reactContext = (ReactContext) getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "routes", event);
    }

    private boolean transportHasStyle(Transport transport) {
        return transport.getLine().getStyle() != null;
    }

    private String formatColor(int color) {
        return String.format("#%06X", (0xFFFFFF & color));
    }

    public void addFeature(@NonNull View child, int index) {
        if (child instanceof final YamapPolygon _child) {
            var obj = _map.getMapObjects().addPolygon(_child.polygon);
            _child.setMapObject(obj);
        } else if (child instanceof final YamapPolyline _child) {
            var obj = _map.getMapObjects().addPolyline(_child.polyline);
            _child.setMapObject(obj);
        } else if (child instanceof final YamapMarker _child) {
            var obj = _map.getMapObjects().addPlacemark(_child.getPoint());
            _child.setMapObject(obj);
        } else if (child instanceof final YamapCircle _child) {
            var obj = _map.getMapObjects().addCircle(_child.circle);
            _child.setMapObject(obj);
        }
    }

    public void removeChild(int index) {
        if (getChildAt(index) instanceof final ReactMapObject child) {
            final var mapObject = child.getMapObject();
            if (mapObject == null || !mapObject.isValid()) return;

            _map.getMapObjects().remove(mapObject);
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull View view) {
        onStart();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull View view) {
        onStop();
    }

    // location listener implementation
    @Override
    public void onObjectAdded(@NonNull UserLocationView userLocationView) {
        if (_userLocationView == userLocationView) return;
        _userLocationView = userLocationView;

        updateUserLocationIcon();
    }

    @Override
    public void onObjectRemoved(@NonNull UserLocationView userLocationView) {
    }

    @Override
    public void onObjectUpdated(@NonNull UserLocationView userLocationView, @NonNull ObjectEvent objectEvent) {
        if (_userLocationView == userLocationView) return;
        _userLocationView = userLocationView;

        updateUserLocationIcon();
    }

    private void updateUserLocationIcon() {
        if (_userLocationView == null) return;

        var userIconStyle = new IconStyle();
        userIconStyle.setScale(_userLocationIconScale);

        var pin = _userLocationView.getPin();
        var arrow = _userLocationView.getArrow();
        if (_userLocationBitmap != null) {
            pin.setIcon(ImageProvider.fromBitmap(_userLocationBitmap), userIconStyle);
            arrow.setIcon(ImageProvider.fromBitmap(_userLocationBitmap), userIconStyle);
        }

        var circle = _userLocationView.getAccuracyCircle();
        if (_userLocationAccuracyFillColor != 0) {
            circle.setFillColor(_userLocationAccuracyFillColor);
        }

        if (_userLocationAccuracyStrokeColor != 0) {
            circle.setStrokeColor(_userLocationAccuracyStrokeColor);
        }

        circle.setStrokeWidth(_userLocationAccuracyStrokeWidth);
    }

    @Override
    public void onCameraPositionChanged(@NonNull com.yandex.mapkit.map.Map map, @NonNull CameraPosition cameraPosition, @NonNull CameraUpdateReason reason, boolean finished) {
        var positionStart = positionToJSON(cameraPosition, reason, finished);
        var reactContext = (ReactContext) getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "cameraPositionChanged", positionStart);
    }

    @Override
    public void onMapTap(@NonNull com.yandex.mapkit.map.Map map, @NonNull Point point) {
        var data = Arguments.createMap();
        data.putDouble("lat", point.getLatitude());
        data.putDouble("lon", point.getLongitude());

        var reactContext = (ReactContext) getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "onMapPress", data);
    }

    @Override
    public void onMapLongTap(@NonNull com.yandex.mapkit.map.Map map, @NonNull Point point) {
        var data = Arguments.createMap();
        data.putDouble("lat", point.getLatitude());
        data.putDouble("lon", point.getLongitude());

        var reactContext = (ReactContext) getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "onMapLongPress", data);
    }

    @Override
    public void onMapLoaded(MapLoadStatistics statistics) {
        var data = Arguments.createMap();
        data.putInt("renderObjectCount", statistics.getRenderObjectCount());
        data.putDouble("curZoomModelsLoaded", statistics.getCurZoomModelsLoaded());
        data.putDouble("curZoomPlacemarksLoaded", statistics.getCurZoomPlacemarksLoaded());
        data.putDouble("curZoomLabelsLoaded", statistics.getCurZoomLabelsLoaded());
        data.putDouble("curZoomGeometryLoaded", statistics.getCurZoomGeometryLoaded());
        data.putDouble("tileMemoryUsage", statistics.getTileMemoryUsage());
        data.putDouble("delayedGeometryLoaded", statistics.getDelayedGeometryLoaded());
        data.putDouble("fullyAppeared", statistics.getFullyAppeared());
        data.putDouble("fullyLoaded", statistics.getFullyLoaded());

        var reactContext = (ReactContext) getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "onMapLoaded", data);
    }

    // trafficListener implementation
    @Override
    public void onTrafficChanged(@Nullable TrafficLevel trafficLevel) {
    }

    @Override
    public void onTrafficLoading() {
    }

    @Override
    public void onTrafficExpired() {
    }
}