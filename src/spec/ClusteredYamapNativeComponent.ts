import {Component} from 'react';
import {codegenNativeComponent, type CodegenTypes, type HostInstance, type ViewProps} from 'react-native';

interface MapLoaded {
  renderObjectCount: CodegenTypes.Double;
  curZoomModelsLoaded: CodegenTypes.Double;
  curZoomPlacemarksLoaded: CodegenTypes.Double;
  curZoomLabelsLoaded: CodegenTypes.Double;
  curZoomGeometryLoaded: CodegenTypes.Double;
  tileMemoryUsage: CodegenTypes.Double;
  delayedGeometryLoaded: CodegenTypes.Double;
  fullyAppeared: CodegenTypes.Double;
  fullyLoaded: CodegenTypes.Double;
}

interface InitialRegion {
  lat: CodegenTypes.Double;
  lon: CodegenTypes.Double;
  zoom?: CodegenTypes.Double;
  azimuth?: CodegenTypes.Double;
  tilt?: CodegenTypes.Double;
}

interface YandexLogoPosition {
  horizontal?: CodegenTypes.WithDefault<'left' | 'center' | 'right', 'left'>;
  vertical?: CodegenTypes.WithDefault<'top' | 'bottom', 'bottom'>;
}

interface YandexLogoPadding {
  horizontal?: CodegenTypes.Double;
  vertical?: CodegenTypes.Double;
}

interface Point {
  lat: CodegenTypes.Double;
  lon: CodegenTypes.Double;
}

interface CameraPositionResponse {
  id: string;
  point: {
    lat: CodegenTypes.Double;
    lon: CodegenTypes.Double;
  }
  azimuth: CodegenTypes.Double;
  finished: boolean;
  reason: string;
  tilt: CodegenTypes.Double;
  zoom: CodegenTypes.Double;
}

type VisibleRegionResponse = {
  id: string;
  bottomLeft: {
    lat: CodegenTypes.Double;
    lon: CodegenTypes.Double;
  };
  bottomRight: {
    lat: CodegenTypes.Double;
    lon: CodegenTypes.Double;
  };
  topLeft: {
    lat: CodegenTypes.Double;
    lon: CodegenTypes.Double;
  };
  topRight: {
    lat: CodegenTypes.Double;
    lon: CodegenTypes.Double;
  };
}

type ScreenPointsResponse = {
  id: string;
  screenPoints: {
    x: CodegenTypes.Double;
    y: CodegenTypes.Double;
  }[]
}

type WorldPointsResponse = {
  id: string;
  worldPoints: {
    lat: CodegenTypes.Double;
    lon: CodegenTypes.Double;
  }[]
}

export interface YandexClusterSizes {
  width?: CodegenTypes.Double;
  height?: CodegenTypes.Double;
}

export interface ClusterPlacemarkPress {
  lat: CodegenTypes.Double;
  lon: CodegenTypes.Double;
  index: CodegenTypes.Int32;
}

export interface ClusteredYamapNativeProps extends ViewProps {
  userLocationIconScale?: CodegenTypes.Float;
  showUserPosition?: boolean;
  nightMode?: boolean;
  mapStyle?: string;
  mapType?: CodegenTypes.WithDefault<'none' | 'raster' | 'vector', 'none'>;
  onCameraPositionChange?: CodegenTypes.DirectEventHandler<CameraPositionResponse>;
  onCameraPositionChangeEnd?: CodegenTypes.DirectEventHandler<CameraPositionResponse>;
  onMapPress?: CodegenTypes.BubblingEventHandler<Point>;
  onMapLongPress?: CodegenTypes.BubblingEventHandler<Point>;
  onMapLoaded?: CodegenTypes.DirectEventHandler<MapLoaded>;
  userLocationAccuracyFillColor?: CodegenTypes.Int32;
  userLocationAccuracyStrokeColor?: CodegenTypes.Int32;
  userLocationAccuracyStrokeWidth?: CodegenTypes.Float;
  scrollGesturesDisabled?: boolean;
  zoomGesturesDisabled?: boolean;
  tiltGesturesDisabled?: boolean;
  rotateGesturesDisabled?: boolean;
  fastTapDisabled?: boolean;
  initialRegion?: InitialRegion;
  followUser?: boolean;
  logoPosition?: YandexLogoPosition;
  logoPadding?: YandexLogoPadding;
  userLocationIcon: string | undefined;
  interactiveDisabled?: boolean;

  onCameraPositionReceived: CodegenTypes.DirectEventHandler<CameraPositionResponse>;
  onVisibleRegionReceived: CodegenTypes.DirectEventHandler<VisibleRegionResponse>;
  onWorldToScreenPointsReceived: CodegenTypes.DirectEventHandler<ScreenPointsResponse>;
  onScreenToWorldPointsReceived: CodegenTypes.DirectEventHandler<WorldPointsResponse>;

  clusteredMarkers: Point[];
  clusterColor?: CodegenTypes.Int32;
  clusterIcon?: string | undefined;
  clusterSize?: YandexClusterSizes;
  clusterTextSize?: CodegenTypes.Float;
  clusterTextYOffset?: CodegenTypes.Int32;
  clusterTextXOffset?: CodegenTypes.Int32;
  clusterTextColor?: CodegenTypes.Int32;
  onClusterPlacemarkPress?: CodegenTypes.BubblingEventHandler<ClusterPlacemarkPress>;
}

export type ClusteredYamapNativeRef = Component<ClusteredYamapNativeProps, {}, any> & HostInstance;

require('./commands/yamap');

export default codegenNativeComponent<ClusteredYamapNativeProps>('ClusteredYamapView');
