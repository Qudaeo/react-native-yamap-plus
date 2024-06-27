package ru.vvdev.yamap.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import androidx.annotation.NonNull;

import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.Cluster;
import com.yandex.mapkit.map.ClusterListener;
import com.yandex.mapkit.map.ClusterTapListener;
import com.yandex.mapkit.map.ClusterizedPlacemarkCollection;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.runtime.image.ImageProvider;

import java.util.ArrayList;
import java.util.HashMap;

public class ClusteredYamapView extends BaseYamapView implements ClusterListener, ClusterTapListener {
    static final double CLUSTER_RADIUS = 50;
    static final int MIN_ZOOM = 12;

    private final ClusterizedPlacemarkCollection _clusterCollection;
    private int _clusterColor = 0;
    private final HashMap<String, PlacemarkMapObject> _placemarksMap = new HashMap<>();
    private ArrayList<Point> _pointsList = new ArrayList<>();

    public ClusteredYamapView(Context context) {
        super(context);

        _clusterCollection = getMapWindow().getMap().getMapObjects().addClusterizedPlacemarkCollection(this);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull View view) {
        super.onViewAttachedToWindow(view);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull View view) {
        super.onViewDetachedFromWindow(view);
    }

    public void setClusteredMarkers(ArrayList<Object> pointObjects) {
        _pointsList = getPoints(pointObjects);

        refreshPoints();
    }

    public void setClustersColor(int color) {
        if (_clusterColor == color) return;
        _clusterColor = color;

        updateUserMarkersColor();
    }

    @Override
    public void addFeature(@NonNull View child, int index) {
        var marker = (YamapMarker) child;
        if (!_placemarksMap.isEmpty()) {
            var key = getPointKey(marker.getPoint());
            var placemark = _placemarksMap.get(key);
            marker.setMapObject(placemark);
        } else {
            marker.setMapObject(null);
        }
    }

    @Override
    public void removeChild(int index) {
        var childView = getChildAt(index);
        if (childView instanceof final YamapMarker child) {
            final var mapObject = child.getMapObject();
            if (mapObject == null || !mapObject.isValid()) return;

            _clusterCollection.remove(mapObject);

            if (!_placemarksMap.isEmpty()) {
                _placemarksMap.remove(getPointKey(child.getPoint()));
            }
        }
    }

    @Override
    public void onClusterAdded(@NonNull Cluster cluster) {
        var text = Integer.toString(cluster.getSize());
        var image = new TextImageProvider(text, _clusterColor);
        cluster.getAppearance().setIcon(image);
        cluster.addClusterTapListener(this);
    }

    @Override
    public boolean onClusterTap(@NonNull Cluster cluster) {
        var placemarks = cluster.getPlacemarks();
        var points = new ArrayList<Point>(placemarks.size());
        for (var placemark : cluster.getPlacemarks()) {
            points.add(placemark.getGeometry());
        }

        fitMarkers(points);

        return true;
    }

    private void refreshPoints() {
        _clusterCollection.clear();
        _placemarksMap.clear();

        if (!_pointsList.isEmpty()) {
            // var image = ImageProvider.fromResource(getContext(), R.drawable.ic_dollar_pin);
            var image = new TextImageProvider("", _clusterColor);
            var placemarks = _clusterCollection.addPlacemarks(_pointsList, image, new IconStyle());

            for (var i = 0; i < placemarks.size(); i++) {
                var placemark = placemarks.get(i);

                var key = getPointKey(placemark.getGeometry());
                _placemarksMap.put(key, placemark);

                var child = getChildAt(i);
                if (child instanceof final YamapMarker marker) {
                    marker.setMapObject(placemark);
                }
            }
        }

        _clusterCollection.clusterPlacemarks(CLUSTER_RADIUS, MIN_ZOOM);
    }

    private void updateUserMarkersColor() {
        refreshPoints();
    }

    private ArrayList<Point> getPoints(ArrayList<Object> pointObjects) {
        if (pointObjects == null || pointObjects.isEmpty()) {
            return new ArrayList<>(0);
        }

        if (pointObjects.size() % 2 != 0) {
            throw new IllegalArgumentException("pointSize % 2 != 0");
        }

        var points = new ArrayList<Point>(pointObjects.size());
        for (var i = 0; i < pointObjects.size(); i += 2) {
            var lat = (Double) pointObjects.get(i);
            var lon = (Double) pointObjects.get(i + 1);

            points.add(new Point(lat, lon));
        }

        return points;
    }

    private String getPointKey(Point point) {
        return "" + point.getLatitude() + point.getLongitude();
    }
}

class TextImageProvider extends ImageProvider {
    private static final float FONT_SIZE = 45;
    private static final float MARGIN_SIZE = 9;
    private static final float STROKE_SIZE = 9;
    private final String _text;
    private final String _id;
    private final int _color;

    public TextImageProvider(String text, int color) {
        this._text = text;
        this._id = "text_" + _text;
        this._color = color;
    }

    @Override
    public String getId() {
        return this._id;
    }

    @Override
    public Bitmap getImage() {
        var textPaint = new Paint();
        textPaint.setTextSize(FONT_SIZE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setAntiAlias(true);

        var widthF = textPaint.measureText(_text);
        var textMetrics = textPaint.getFontMetrics();
        var heightF = Math.abs(textMetrics.bottom) + Math.abs(textMetrics.top);
        var textRadius = (float) Math.sqrt(widthF * widthF + heightF * heightF) / 2;
        var internalRadius = textRadius + MARGIN_SIZE;
        var externalRadius = internalRadius + STROKE_SIZE;

        final var width = (int) (2 * externalRadius + 0.5);
        final var halfWidth = (float) width / 2;

        var bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        var canvas = new Canvas(bitmap);

        var backgroundPaint = new Paint();
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setColor(_color);
        canvas.drawCircle(halfWidth, halfWidth, externalRadius, backgroundPaint);

        backgroundPaint.setColor(Color.WHITE);
        canvas.drawCircle(halfWidth, halfWidth, internalRadius, backgroundPaint);

        canvas.drawText(
                _text,
                halfWidth,
                halfWidth - (textMetrics.ascent + textMetrics.descent) / 2,
                textPaint);

        return bitmap;
    }
}