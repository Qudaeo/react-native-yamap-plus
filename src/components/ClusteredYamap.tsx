import React from 'react';
import {
  Platform,
  requireNativeComponent,
  NativeModules,
  UIManager,
  findNodeHandle,
  ViewProps,
  ImageSourcePropType, NativeSyntheticEvent, ListRenderItemInfo,
} from 'react-native';
// @ts-ignore
import resolveAssetSource from 'react-native/Libraries/Image/resolveAssetSource';
import CallbacksManager from '../utils/CallbacksManager';
import { MapType, Animation, Point, DrivingInfo, MasstransitInfo, RoutesFoundEvent, Vehicles, CameraPosition, VisibleRegion } from '../interfaces';
import { processColorProps } from '../utils';
import { YaMap } from './Yamap';

const { yamap: NativeYamapModule } = NativeModules;

export interface ClusteredYaMapProps<T = any> extends ViewProps {
  userLocationIcon?: ImageSourcePropType;
  clusteredMarkers: ReadonlyArray<{point: Point, data: T}>
  renderMarker: (info: {point: Point, data: ListRenderItemInfo<T>}, index: number) => React.ReactElement
  clusterColor?: string;
  showUserPosition?: boolean;
  nightMode?: boolean;
  mapStyle?: string;
  mapType?: MapType;
  onCameraPositionChange?: (event: NativeSyntheticEvent<CameraPosition>) => void;
  onMapPress?: (event: NativeSyntheticEvent<Point>) => void;
  onMapLongPress?: (event: NativeSyntheticEvent<Point>) => void;
  userLocationAccuracyFillColor?: string;
  userLocationAccuracyStrokeColor?: string;
  userLocationAccuracyStrokeWidth?: number;
  scrollGesturesEnabled?: boolean;
  zoomGesturesEnabled?: boolean;
  tiltGesturesEnabled?: boolean;
  rotateGesturesEnabled?: boolean;
  fastTapEnabled?: boolean;
}

const YaMapNativeComponent = requireNativeComponent<Omit<ClusteredYaMapProps, 'clusteredMarkers'> & {clusteredMarkers: Point[]}>('ClusteredYamapView');

export class ClusteredYamap extends React.Component<ClusteredYaMapProps> {
  static defaultProps = {
    showUserPosition: true,
    clusterColor: 'red',
  };

  // @ts-ignore
  map = React.createRef<YaMapNativeComponent>();

  static ALL_MASSTRANSIT_VEHICLES: Vehicles[] = [
    'bus',
    'trolleybus',
    'tramway',
    'minibus',
    'suburban',
    'underground',
    'ferry',
    'cable',
    'funicular',
  ];

  public static init(apiKey: string) {
    NativeYamapModule.init(apiKey);
  }

  public static setLocale(locale: string): Promise<void> {
    return new Promise((resolve, reject) => {
      NativeYamapModule.setLocale(locale, () => resolve(), (err: string) => reject(new Error(err)));
    });
  }

  public static getLocale(): Promise<string> {
    return new Promise((resolve, reject) => {
      NativeYamapModule.getLocale((locale: string) => resolve(locale), (err: string) => reject(new Error(err)));
    });
  }

  public static resetLocale(): Promise<void> {
    return new Promise((resolve, reject) => {
      NativeYamapModule.resetLocale(() => resolve(), (err: string) => reject(new Error(err)));
    });
  }

  public findRoutes(points: Point[], vehicles: Vehicles[], callback: (event: RoutesFoundEvent<DrivingInfo | MasstransitInfo>) => void) {
    this._findRoutes(points, vehicles, callback);
  }

  public findMasstransitRoutes(points: Point[], callback: (event: RoutesFoundEvent<MasstransitInfo>) => void) {
    this._findRoutes(points, YaMap.ALL_MASSTRANSIT_VEHICLES, callback);
  }

  public findPedestrianRoutes(points: Point[], callback: (event: RoutesFoundEvent<MasstransitInfo>) => void) {
    this._findRoutes(points, [], callback);
  }

  public findDrivingRoutes(points: Point[], callback: (event: RoutesFoundEvent<DrivingInfo>) => void) {
    this._findRoutes(points, ['car'], callback);
  }

  public fitAllMarkers() {
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this),
      this.getCommand('fitAllMarkers'),
      [],
    );
  }

  public setTrafficVisible(isVisible: boolean) {
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this),
      this.getCommand('setTrafficVisible'),
      [isVisible],
    );
  }

  public setCenter(center: { lon: number, lat: number, zoom?: number }, zoom: number = center.zoom || 10, azimuth: number = 0, tilt: number = 0, duration: number = 0, animation: Animation = Animation.SMOOTH) {
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this),
      this.getCommand('setCenter'),
      [center, zoom, azimuth, tilt, duration, animation],
    );
  }

  public setZoom(zoom: number, duration: number = 0, animation: Animation = Animation.SMOOTH) {
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this),
      this.getCommand('setZoom'),
      [zoom, duration, animation],
    );
  }

  public getCameraPosition(callback: (position: CameraPosition) => void) {
    const cbId = CallbacksManager.addCallback(callback);
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this),
      this.getCommand('getCameraPosition'),
      [cbId],
    );
  }

  public getVisibleRegion(callback: (VisibleRegion: VisibleRegion) => void) {
    const callbackId = CallbacksManager.addCallback(callback);
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this),
      this.getCommand('getVisibleRegion'),
      [callbackId],
    );
  }

  private _findRoutes(points: Point[], vehicles: Vehicles[], callback: ((event: RoutesFoundEvent<DrivingInfo | MasstransitInfo>) => void) | ((event: RoutesFoundEvent<DrivingInfo>) => void) | ((event: RoutesFoundEvent<MasstransitInfo>) => void)) {
    const cbId = CallbacksManager.addCallback(callback);
    const args
      = Platform.OS === 'ios'
        ? [{ points, vehicles, id: cbId }]
        : [points, vehicles, cbId];
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this),
      this.getCommand('findRoutes'),
      args,
    );
  }

  private getCommand(cmd: string): any {
    if (Platform.OS === 'ios') {
      return UIManager.getViewManagerConfig('ClusteredYamapView').Commands[cmd];
    } else {
      return cmd;
    }
  }

  private processRoute(event: any) {
    CallbacksManager.call(event.nativeEvent.id, event);
  }

  private processCameraPosition(event: any) {
    const { id, ...position } = event.nativeEvent;
    CallbacksManager.call(id, position);
  }

  private processVisibleRegion(event: NativeSyntheticEvent<{id: string} & VisibleRegion>) {
    const { id, ...visibleRegion } = event.nativeEvent;
    CallbacksManager.call(id, visibleRegion);
  }

  private resolveImageUri(img: ImageSourcePropType) {
    return img ? resolveAssetSource(img).uri : '';
  }

  private getProps() {
    const props = {
      ...this.props,
      clusteredMarkers: this.props.clusteredMarkers.map(mark => mark.point),
      children: this.props.clusteredMarkers.map(this.props.renderMarker),
      onRouteFound: this.processRoute,
      onCameraPositionReceived: this.processCameraPosition,
      onVisibleRegionReceived: this.processVisibleRegion,
      userLocationIcon: this.props.userLocationIcon ? this.resolveImageUri(this.props.userLocationIcon) : undefined,
    };
    processColorProps(props, 'clusterColor' as keyof ClusteredYaMapProps);
    processColorProps(props, 'userLocationAccuracyFillColor' as keyof ClusteredYaMapProps);
    processColorProps(props, 'userLocationAccuracyStrokeColor' as keyof ClusteredYaMapProps);
    return props;
  }

  render() {
    return (
      <YaMapNativeComponent
        {...this.getProps()}
        ref={this.map}
      />
    );
  }
}
