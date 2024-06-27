import { type RefObject, useCallback, useMemo } from 'react';
import { Image, Platform, UIManager, findNodeHandle, processColor } from 'react-native';

import type {
  FindDrivingRoutesFunc,
  FindMasstransitRoutesFunc,
  FindPedestrianRoutesFunc,
  YamapNativeRef,
  YamapProps,
  YamapRef,
  onCameraPositionReceivedFunc,
  onRouteFunc,
  onScreenToWorldPointsFunc,
  onVisibleRegionFunc,
  onWorldToScreenPointsFunc
} from '../components/Yamap/types';
import { YamapViewManagerMethod } from '../components/Yamap/types';
import { Animation, type Vehicles } from '../interfaces';
import CallbacksManager from '../utils/CallbacksManager';

const ALL_MASSTRANSIT_VEHICLES: Vehicles[] = [
  'bus',
  'trolleybus',
  'tramway',
  'minibus',
  'suburban',
  'underground',
  'ferry',
  'cable',
  'funicular',
] as const;

export type UseYamapProps = Pick<YamapProps,
  'userLocationIcon' |
  'userLocationAccuracyFillColor' |
  'userLocationAccuracyStrokeColor'
>;

export const useYamap = (componentName: string, nativeRef: RefObject<YamapNativeRef>, {
  userLocationIcon,
  userLocationAccuracyFillColor,
  userLocationAccuracyStrokeColor,
}: UseYamapProps) => {
  const resolvedUserLocationIcon = useMemo(() => userLocationIcon ? Image.resolveAssetSource(userLocationIcon) : undefined, [userLocationIcon]);
  const processedUserLocationAccuracyFillColor = useMemo(() => processColor(userLocationAccuracyFillColor), [userLocationAccuracyFillColor]);
  const processedUserLocationAccuracyStrokeColor = useMemo(() => processColor(userLocationAccuracyStrokeColor), [userLocationAccuracyStrokeColor]);

  const getCommand = useCallback((cmd: string) => {
    return Platform.OS === 'ios' ? UIManager.getViewManagerConfig(componentName).Commands[cmd] : cmd;
  }, [componentName]);

  const setCenter = useCallback<YamapRef[YamapViewManagerMethod.setCenter]>((center, zoom = 0, azimuth = 0, tilt = 0, duration = 0, animation = Animation.SMOOTH) => {
    UIManager.dispatchViewManagerCommand(findNodeHandle(nativeRef.current), getCommand(YamapViewManagerMethod.setCenter), [center, zoom, azimuth, tilt, duration, animation]);
  }, [getCommand, nativeRef]);

  const fitAllMarkers = useCallback<YamapRef[YamapViewManagerMethod.fitAllMarkers]>(() => {
    UIManager.dispatchViewManagerCommand(findNodeHandle(nativeRef.current), getCommand(YamapViewManagerMethod.fitAllMarkers), []);
  }, [getCommand, nativeRef]);

  const fitMarkers = useCallback<YamapRef[YamapViewManagerMethod.fitMarkers]>((points) => {
    UIManager.dispatchViewManagerCommand(findNodeHandle(nativeRef.current), getCommand(YamapViewManagerMethod.fitMarkers), [points]);
  }, [getCommand, nativeRef]);

  const findRoutes = useCallback<YamapRef[YamapViewManagerMethod.findRoutes]>((points, vehicles, callback) => {
    const cbId = CallbacksManager.addCallback(callback);
    const args = Platform.OS === 'ios' ? [{ points, vehicles, id: cbId }] : [points, vehicles, cbId];
    UIManager.dispatchViewManagerCommand(findNodeHandle(nativeRef.current), getCommand(YamapViewManagerMethod.findRoutes), args);
  }, [getCommand, nativeRef]);

  const setZoom = useCallback<YamapRef[YamapViewManagerMethod.setZoom]>((zoom, duration = 0, animation = Animation.SMOOTH) => {
    UIManager.dispatchViewManagerCommand(findNodeHandle(nativeRef.current), getCommand(YamapViewManagerMethod.setZoom), [zoom, duration, animation]);
  }, [getCommand, nativeRef]);

  const getCameraPosition = useCallback<YamapRef[YamapViewManagerMethod.getCameraPosition]>((callback) => {
    const cbId = CallbacksManager.addCallback(callback);
    UIManager.dispatchViewManagerCommand(findNodeHandle(nativeRef.current), getCommand(YamapViewManagerMethod.getCameraPosition), [cbId]);
  }, [getCommand, nativeRef]);

  const getVisibleRegion = useCallback<YamapRef[YamapViewManagerMethod.getVisibleRegion]>((callback) => {
    const cbId = CallbacksManager.addCallback(callback);
    UIManager.dispatchViewManagerCommand(findNodeHandle(nativeRef.current), getCommand(YamapViewManagerMethod.getVisibleRegion), [cbId]);
  }, [getCommand, nativeRef]);

  const setTrafficVisible = useCallback<YamapRef[YamapViewManagerMethod.setTrafficVisible]>((isVisible) => {
    UIManager.dispatchViewManagerCommand(findNodeHandle(nativeRef.current), getCommand(YamapViewManagerMethod.setTrafficVisible), [isVisible]);
  }, [getCommand, nativeRef]);

  const getScreenPoints = useCallback<YamapRef[YamapViewManagerMethod.getScreenPoints]>((points, callback) => {
    const cbId = CallbacksManager.addCallback(callback);
    UIManager.dispatchViewManagerCommand(findNodeHandle(nativeRef.current), getCommand(YamapViewManagerMethod.getScreenPoints), [points, cbId]);
  }, [getCommand, nativeRef]);

  const getWorldPoints = useCallback<YamapRef[YamapViewManagerMethod.getWorldPoints]>((screenPoints, callback) => {
    const cbId = CallbacksManager.addCallback(callback);
    UIManager.dispatchViewManagerCommand(findNodeHandle(nativeRef.current), getCommand(YamapViewManagerMethod.getWorldPoints), [screenPoints, cbId]);
  }, [getCommand, nativeRef]);

  const findMasstransitRoutes = useCallback<FindMasstransitRoutesFunc>((points, callback) => {
    findRoutes(points, ALL_MASSTRANSIT_VEHICLES, callback);
  }, [findRoutes]);

  const findPedestrianRoutes = useCallback<FindPedestrianRoutesFunc>((points, callback) => {
    findRoutes(points, [], callback);
  }, [findRoutes]);

  const findDrivingRoutes = useCallback<FindDrivingRoutesFunc>((points, callback) => {
    findRoutes(points, ['car'], callback);
  }, [findRoutes]);

  const onRouteFound = useCallback<onRouteFunc>((event) => {
    const { id, ...routes } = event.nativeEvent;
    CallbacksManager.call(id, routes);
  }, []);

  const onCameraPositionReceived = useCallback<onCameraPositionReceivedFunc>((event) => {
    const { id, ...point } = event.nativeEvent;
    CallbacksManager.call(id, point);
  }, []);

  const onVisibleRegionReceived = useCallback<onVisibleRegionFunc>((event) => {
    const { id, ...visibleRegion } = event.nativeEvent;
    CallbacksManager.call(id, visibleRegion);
  }, []);

  const onWorldToScreenPointsReceived = useCallback<onWorldToScreenPointsFunc>((event) => {
    const { id, screenPoints } = event.nativeEvent;
    CallbacksManager.call(id, screenPoints);
  }, []);

  const onScreenToWorldPointsReceived = useCallback<onScreenToWorldPointsFunc>((event) => {
    const { id, worldPoints } = event.nativeEvent;
    CallbacksManager.call(id, worldPoints);
  }, []);

  const result = useMemo(() => ({
    resolvedUserLocationIcon,
    processedUserLocationAccuracyFillColor,
    processedUserLocationAccuracyStrokeColor,

    setCenter,
    fitAllMarkers,
    fitMarkers,
    findRoutes,
    setZoom,
    getCameraPosition,
    getVisibleRegion,
    setTrafficVisible,
    getScreenPoints,
    getWorldPoints,

    findMasstransitRoutes,
    findPedestrianRoutes,
    findDrivingRoutes,

    onRouteFound,
    onCameraPositionReceived,
    onVisibleRegionReceived,
    onWorldToScreenPointsReceived,
    onScreenToWorldPointsReceived,
  }), [
    resolvedUserLocationIcon,
    processedUserLocationAccuracyFillColor,
    processedUserLocationAccuracyStrokeColor,

    setCenter,
    fitAllMarkers,
    fitMarkers,
    findRoutes,
    setZoom,
    getCameraPosition,
    getVisibleRegion,
    setTrafficVisible,
    getScreenPoints,
    getWorldPoints,

    findMasstransitRoutes,
    findPedestrianRoutes,
    findDrivingRoutes,

    onRouteFound,
    onCameraPositionReceived,
    onVisibleRegionReceived,
    onWorldToScreenPointsReceived,
    onScreenToWorldPointsReceived,
  ]);

  return result;
};
