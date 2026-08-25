import {Component} from 'react';
import {codegenNativeComponent, type HostInstance, type CodegenTypes, type ViewProps} from 'react-native';

export interface InitialRegion {
  lat: CodegenTypes.Double;
  lon: CodegenTypes.Double;
  zoom?: CodegenTypes.Double;
  azimuth?: CodegenTypes.Double;
  tilt?: CodegenTypes.Double;
}

export interface YandexLogoPosition {
  horizontal?: CodegenTypes.WithDefault<'left' | 'center' | 'right', 'left'>;
  vertical?: CodegenTypes.WithDefault<'top' | 'bottom', 'bottom'>;
}

export interface YandexLogoPadding {
  horizontal?: CodegenTypes.Double;
  vertical?: CodegenTypes.Double;
}

export interface Point {
  lat: CodegenTypes.Double;
  lon: CodegenTypes.Double;
}

export interface CameraPositionNativeResponse {
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

export type VisibleRegionNativeResponse = {
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

export type ScreenPointsNativeResponse = {
  id: string;
  screenPoints: {
    x: CodegenTypes.Double;
    y: CodegenTypes.Double;
  }[]
}

export type WorldPointsNativeResponse = {
  id: string;
  worldPoints: {
    lat: CodegenTypes.Double;
    lon: CodegenTypes.Double;
  }[]
}

export interface MapLoaded {
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

export interface YamapNativeProps extends ViewProps {
  userLocationIconScale?: CodegenTypes.Float;
  showUserPosition?: boolean;
  nightMode?: boolean;
  mapStyle?: string;
  mapType?: CodegenTypes.WithDefault<'none' | 'raster' | 'vector', 'vector'>;
  onCameraPositionChange?: CodegenTypes.DirectEventHandler<CameraPositionNativeResponse>;
  onCameraPositionChangeEnd?: CodegenTypes.DirectEventHandler<CameraPositionNativeResponse>;
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

  onCameraPositionReceived: CodegenTypes.DirectEventHandler<CameraPositionNativeResponse>;
  onVisibleRegionReceived: CodegenTypes.DirectEventHandler<VisibleRegionNativeResponse>;
  onWorldToScreenPointsReceived: CodegenTypes.DirectEventHandler<ScreenPointsNativeResponse>;
  onScreenToWorldPointsReceived: CodegenTypes.DirectEventHandler<WorldPointsNativeResponse>;
}

export type YamapNativeRef = Component<YamapNativeProps, {}, any> & HostInstance;

require('./commands/yamap');

export default codegenNativeComponent<YamapNativeProps>('YamapView');
