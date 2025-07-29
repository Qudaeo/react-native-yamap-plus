import codegenNativeComponent, {NativeComponentType} from 'react-native/Libraries/Utilities/codegenNativeComponent';
import {YamapNativeCommands} from './commands/yamap';
import {
  BubblingEventHandler,
  DirectEventHandler,
  Double,
  Float,
  Int32,
  WithDefault,
} from 'react-native/Libraries/Types/CodegenTypes';
import {NativeMethods, ViewProps} from 'react-native';
import {Component} from 'react';

interface InitialRegion {
  lat: Double;
  lon: Double;
  zoom?: Double;
  azimuth?: Double;
  tilt?: Double;
}

interface YandexLogoPosition {
  horizontal?: WithDefault<'left' | 'center' | 'right', 'left'>;
  vertical?: WithDefault<'top' | 'bottom', 'bottom'>;
}

interface YandexLogoPadding {
  horizontal?: Double;
  vertical?: Double;
}

interface Point {
  lat: Double;
  lon: Double;
}

export interface CameraPosition {
  id: string;
  point: {
    lat: Double;
    lon: Double;
  }
  azimuth: Double;
  finished: boolean;
  reason: string;
  tilt: Double;
  zoom: Double;
}

export type VisibleRegion = {
  id: string;
  bottomLeft: {
    lat: Double;
    lon: Double;
  };
  bottomRight: {
    lat: Double;
    lon: Double;
  };
  topLeft: {
    lat: Double;
    lon: Double;
  };
  topRight: {
    lat: Double;
    lon: Double;
  };
}

export type ScreenPointResponse = {
  id: string;
  screenPoints: {
    x: Double;
    y: Double;
  }[]
}

export interface MapLoaded {
  renderObjectCount: Double;
  curZoomModelsLoaded: Double;
  curZoomPlacemarksLoaded: Double;
  curZoomLabelsLoaded: Double;
  curZoomGeometryLoaded: Double;
  tileMemoryUsage: Double;
  delayedGeometryLoaded: Double;
  fullyAppeared: Double;
  fullyLoaded: Double;
}

export interface YamapNativeProps extends ViewProps {
  userLocationIconScale?: Float;
  showUserPosition?: boolean;
  nightMode?: boolean;
  mapStyle?: string;
  mapType?: WithDefault<'none' | 'raster' | 'vector', 'vector'>;
  onCameraPositionChange?: DirectEventHandler<CameraPosition>;
  onCameraPositionChangeEnd?: DirectEventHandler<CameraPosition>;
  onMapPress?: BubblingEventHandler<Point>;
  onMapLongPress?: BubblingEventHandler<Point>;
  onMapLoaded?: DirectEventHandler<MapLoaded>;
  userLocationAccuracyFillColor?: Int32;
  userLocationAccuracyStrokeColor?: Int32;
  userLocationAccuracyStrokeWidth?: Float;
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

  onCameraPositionReceived: DirectEventHandler<CameraPosition>;
  onVisibleRegionReceived: DirectEventHandler<VisibleRegion>;
  onWorldToScreenPointsReceived: DirectEventHandler<ScreenPointResponse>;
  onScreenToWorldPointsReceived: DirectEventHandler<undefined>;
}

export type YamapNativeRef = Component<YamapNativeProps, {}, any> & Readonly<NativeMethods>
export type YamapComponentType = NativeComponentType<YamapNativeProps> & Readonly<YamapNativeCommands>;

require('./commands/yamap');

export default codegenNativeComponent<YamapNativeProps>('YamapView');
