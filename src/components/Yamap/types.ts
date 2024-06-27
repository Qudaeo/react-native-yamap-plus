import type { Component, RefObject } from 'react';
import { type ImageSourcePropType, type NativeMethods, type NativeSyntheticEvent, type ProcessedColorValue, type ViewProps } from 'react-native';

import type {
  Animation,
  CameraPosition,
  DrivingInfo,
  InitialRegion,
  MapLoaded,
  MapType,
  MasstransitInfo,
  Point,
  RoutesFoundEvent,
  ScreenPoint,
  Vehicles,
  VisibleRegion,
  YandexLogoPadding,
  YandexLogoPosition
} from '../../interfaces';
import type { OmitEx } from '../../utils/types';

export type RoutesFoundCallback = RoutedMasstransitCallback | RoutedDrivingCallback;
export type RoutedMasstransitCallback = (event: RoutesFoundEvent<MasstransitInfo>) => void;
export type RoutedDrivingCallback = (event: RoutesFoundEvent<DrivingInfo>) => void;
export type CameraPositionCallback = (position: CameraPosition) => void;
export type VisibleRegionCallback = (visibleRegion: VisibleRegion) => void;
export type ScreenPointCallback = (points: ReadonlyArray<Point>) => void;
export type WorldPointCallback = (points: ReadonlyArray<Point>) => void;

export type SetCenterFunc = (center: Point, zoom?: number, azimuth?: number, tilt?: number, duration?: number, animation?: Animation) => void;
export type FitAllMarkersFunc = () => void;
export type FitMarkersFunc = (points: ReadonlyArray<Point>) => void;
export type FindRoutesFunc = (points: ReadonlyArray<Point>, vehicles: ReadonlyArray<Vehicles>, callback: RoutesFoundCallback) => void;
export type SetZoomFunc = (zoom: number, duration?: number, animation?: Animation) => void;
export type GetCameraPositionFunc = (callback: CameraPositionCallback) => void;
export type GetVisibleRegionFunc = (callback: VisibleRegionCallback) => void;
export type SetTrafficVisibleFunc = (isVisible: boolean) => void;
export type GetScreenPointsFunc = (points: ReadonlyArray<Point>, callback: ScreenPointCallback) => void;
export type GetWorldPointsFunc = (screenPoints: ReadonlyArray<ScreenPoint>, callback: WorldPointCallback) => void;
export type FindMasstransitRoutesFunc = (points: ReadonlyArray<Point>, callback: RoutedMasstransitCallback) => void;
export type FindPedestrianRoutesFunc = (points: ReadonlyArray<Point>, callback: RoutedMasstransitCallback) => void;
export type FindDrivingRoutesFunc = (points: ReadonlyArray<Point>, callback: RoutedDrivingCallback) => void;

export type onCameraPositionChangedFunc = (event: NativeSyntheticEvent<CameraPosition>) => void;
export type onMapLoadedFunc = (event: NativeSyntheticEvent<MapLoaded>) => void;
export type onMapPressFunc = (event: NativeSyntheticEvent<Point>) => void;
export type onMapLongPressFunc = (event: NativeSyntheticEvent<Point>) => void;

export type onRouteFunc = (event: NativeSyntheticEvent<{ id: string } & RoutesFoundCallback>) => void;
export type onCameraPositionReceivedFunc = (event: NativeSyntheticEvent<{ id: string } & CameraPosition>) => void;
export type onVisibleRegionFunc = (event: NativeSyntheticEvent<{ id: string } & VisibleRegion>) => void;
export type onWorldToScreenPointsFunc = (event: NativeSyntheticEvent<{ id: string; screenPoints: ScreenPoint[] }>) => void;
export type onScreenToWorldPointsFunc = (event: NativeSyntheticEvent<{ id: string; worldPoints: Point[] }>) => void;

export interface YamapProps extends ViewProps {
  userLocationIcon?: ImageSourcePropType;
  userLocationIconScale?: number;
  showUserPosition?: boolean;
  nightMode?: boolean;
  mapStyle?: string;
  mapType?: MapType;
  userLocationAccuracyFillColor?: string;
  userLocationAccuracyStrokeColor?: string;
  userLocationAccuracyStrokeWidth?: number;
  scrollGesturesEnabled?: boolean;
  zoomGesturesEnabled?: boolean;
  tiltGesturesEnabled?: boolean;
  rotateGesturesEnabled?: boolean;
  fastTapEnabled?: boolean;
  initialRegion?: InitialRegion;
  followUser?: boolean;
  logoPosition?: YandexLogoPosition;
  logoPadding?: YandexLogoPadding;

  onMapLoaded?: onMapLoadedFunc;
  onMapPress?: onMapPressFunc;
  onMapLongPress?: onMapLongPressFunc;

  onCameraPositionChanged?: onCameraPositionChangedFunc;
}

type OmitYamapProps = OmitEx<YamapProps,
  'userLocationIcon' |
  'userLocationAccuracyFillColor' |
  'userLocationAccuracyStrokeColor'
>;

export type YamapNativeComponentProps = OmitYamapProps & {
  userLocationIcon?: string | null | undefined;
  userLocationAccuracyFillColor?: ProcessedColorValue | null | undefined;
  userLocationAccuracyStrokeColor?: ProcessedColorValue | null | undefined;

  onRouteFound?: onRouteFunc;
  onCameraPositionReceived?: onCameraPositionReceivedFunc;
  onVisibleRegionReceived?: onVisibleRegionFunc;
  onWorldToScreenPointsReceived?: onWorldToScreenPointsFunc;
  onScreenToWorldPointsReceived?: onScreenToWorldPointsFunc;
};

export type YamapNativeRef = Component<YamapNativeComponentProps, object, any> & Readonly<NativeMethods>;

export type YamapRef = {
  nativeRef: RefObject<YamapNativeRef>;

  setCenter: SetCenterFunc;
  fitAllMarkers: FitAllMarkersFunc;
  fitMarkers: FitMarkersFunc;
  findRoutes: FindRoutesFunc;
  setZoom: SetZoomFunc;
  getCameraPosition: GetCameraPositionFunc;
  getVisibleRegion: GetVisibleRegionFunc;
  setTrafficVisible: SetTrafficVisibleFunc;
  getScreenPoints: GetScreenPointsFunc;
  getWorldPoints: GetWorldPointsFunc;

  findMasstransitRoutes: FindMasstransitRoutesFunc;
  findPedestrianRoutes: FindPedestrianRoutesFunc;
  findDrivingRoutes: FindDrivingRoutesFunc;
};

export enum YamapViewManagerMethod {
  setCenter = 'setCenter',
  fitAllMarkers = 'fitAllMarkers',
  fitMarkers = 'fitMarkers',
  findRoutes = 'findRoutes',
  setZoom = 'setZoom',
  getCameraPosition = 'getCameraPosition',
  getVisibleRegion = 'getVisibleRegion',
  setTrafficVisible = 'setTrafficVisible',
  getScreenPoints = 'getScreenPoints',
  getWorldPoints = 'getWorldPoints',
}
