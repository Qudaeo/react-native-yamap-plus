package ru.vvdev.yamap.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.facebook.react.views.view.ReactViewGroup;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.RotationType;
import com.yandex.runtime.image.ImageProvider;

import java.util.ArrayList;
import java.util.Objects;

import ru.vvdev.yamap.models.ReactMapObject;

public class YamapMarker extends ReactViewGroup implements MapObjectTapListener, ReactMapObject {
    //    private final int YAMAP_FRAMES_PER_SECOND = 25;

    private Point _point;

    private int _zIndex = 1;
    private float _scale = 1;
    private Boolean _visible = true;
    private Boolean _rotated = false;
    private PointF _anchor = null;
    private String _iconSource;
    private View _childView;
    private PlacemarkMapObject _mapObject;
    private final ArrayList<View> _childs = new ArrayList<>();

    private final OnLayoutChangeListener _onLayoutChangeListener = (v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> updateMarker();

    public YamapMarker(Context context) {
        super(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    }

    // PROPS
    public void setPoint(Point point) {
        if (this._point == point) return;
        this._point = point;

        updateMarker();
    }

    public Point getPoint() {
        return _point;
    }

    public void setZIndex(int zIndex) {
        if (_zIndex == zIndex) return;
        _zIndex = zIndex;

        updateMarker();
    }

    public void setScale(float scale) {
        if (_scale == scale) return;
        _scale = scale;

        updateMarker();
    }

    public void setRotated(Boolean rotated) {
        if (_rotated == rotated) return;
        _rotated = rotated;

        updateMarker();
    }

    public void setVisible(Boolean visible) {
        if (_visible == visible) return;
        _visible = visible;

        updateMarker();
    }

    public void setIconSource(String source) {
        if (Objects.equals(_iconSource, source)) return;
        _iconSource = source;

        updateMarker();
    }

    public void setAnchor(PointF anchor) {
        if (_anchor == anchor) return;
        _anchor = anchor;

        updateMarker();
    }

    public void updateMarker() {
        if (_mapObject == null || !_mapObject.isValid()) return;

        final var iconStyle = new IconStyle();
        iconStyle.setScale(_scale);
        iconStyle.setRotationType(_rotated ? RotationType.ROTATE : RotationType.NO_ROTATION);
        iconStyle.setVisible(_visible);

        if (_anchor != null) {
            iconStyle.setAnchor(_anchor);
        }
        _mapObject.setGeometry(_point);
        _mapObject.setZIndex(_zIndex);
        _mapObject.setIconStyle(iconStyle);

        if (_childView != null) {
            try {
                var b = Bitmap.createBitmap(_childView.getWidth(), _childView.getHeight(), Bitmap.Config.ARGB_8888);
                var c = new Canvas(b);
                _childView.draw(c);
                _mapObject.setIcon(ImageProvider.fromBitmap(b));
                _mapObject.setIconStyle(iconStyle);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (_childs.isEmpty() && !_iconSource.isEmpty()) {
            var parentView = getParent();
            if (parentView instanceof final BaseYamapView view) {
                view.setImage(_iconSource, _mapObject, iconStyle);
            }
        }
    }

    public MapObject getMapObject() {
        return _mapObject;
    }

    public void setMapObject(MapObject obj) {
        if (_mapObject == obj) return;

        _mapObject = (PlacemarkMapObject) obj;
        _mapObject.addTapListener(this);

        updateMarker();
    }

    public void setChildView(View view) {
        if (_childView == view) return;

        if (view == null) {
            _childView.removeOnLayoutChangeListener(_onLayoutChangeListener);
            _childView = null;
            updateMarker();
        } else {
            _childView = view;
            _childView.addOnLayoutChangeListener(_onLayoutChangeListener);
        }
    }

    public void addChildView(View view, int index) {
        _childs.add(index, view);
        setChildView(_childs.get(0));
    }

    public void removeChildView(int index) {
        _childs.remove(index);
        setChildView(!_childs.isEmpty() ? _childs.get(0) : null);
    }

    public void moveAnimationLoop(double lat, double lon) {
        var placemark = (PlacemarkMapObject) this.getMapObject();
        placemark.setGeometry(new Point(lat, lon));
    }

    public void rotateAnimationLoop(float delta) {
        var placemark = (PlacemarkMapObject) this.getMapObject();
        placemark.setDirection(delta);
    }

    public void animatedMoveTo(Point point, final float duration) {
        var placemark = (PlacemarkMapObject) this.getMapObject();
        var p = placemark.getGeometry();

        final var startLat = p.getLatitude();
        final var startLon = p.getLongitude();
        final var deltaLat = point.getLatitude() - startLat;
        final var deltaLon = point.getLongitude() - startLon;

        var valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration((long) duration);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(animation -> {
            try {
                var v = animation.getAnimatedFraction();
                moveAnimationLoop(startLat + v * deltaLat, startLon + v * deltaLon);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        valueAnimator.start();
    }

    public void animatedRotateTo(final float angle, float duration) {
        var placemark = (PlacemarkMapObject) this.getMapObject();
        final var startDirection = placemark.getDirection();
        final var delta = angle - placemark.getDirection();

        var valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration((long) duration);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(animation -> {
            try {
                var v = animation.getAnimatedFraction();
                rotateAnimationLoop(startDirection + v * delta);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        valueAnimator.start();
    }

    @Override
    public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {
        var e = Arguments.createMap();
        ((ReactContext) getContext()).getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "onPress", e);

        return false;
    }
}