import React, {forwardRef, useImperativeHandle, useMemo, useRef} from 'react';
import {getImageUri, getProcessedColors} from '../../utils';
import {YamapProps, YamapRef} from './types';
import {
  onCameraPositionReceived,
  onRouteFound,
  onScreenToWorldPointsReceived,
  onVisibleRegionReceived,
  onWorldToScreenPointsReceived,
} from './events';
import {useYamap} from '../../hooks/useYamap';
import {YamapNativeComponent, YamapNativeRef} from './YamapNativeComponent';

export const YaMap = forwardRef<YamapRef, YamapProps>(({
    showUserPosition = true,
    ...props
  }, ref) => {

  const mapRef = useRef<YamapNativeRef | null>(null);

  const {
    findRoutes,
    findMasstransitRoutes,
    findPedestrianRoutes,
    findDrivingRoutes,
    fitAllMarkers,
    fitMarkers,
    setTrafficVisible,
    setCenter,
    setZoom,
    getCameraPosition,
    getVisibleRegion,
    getScreenPoints,
    getWorldPoints,
  } = useYamap(mapRef);

  useImperativeHandle(ref, () => ({
    findRoutes,
    findMasstransitRoutes,
    findPedestrianRoutes,
    findDrivingRoutes,
    fitAllMarkers,
    fitMarkers,
    setTrafficVisible,
    setCenter,
    setZoom,
    getCameraPosition,
    getVisibleRegion,
    getScreenPoints,
    getWorldPoints,
  }), [
    findRoutes,
    findMasstransitRoutes,
    findPedestrianRoutes,
    findDrivingRoutes,
    fitAllMarkers,
    fitMarkers,
    setTrafficVisible,
    setCenter,
    setZoom,
    getCameraPosition,
    getVisibleRegion,
    getScreenPoints,
    getWorldPoints,
  ]);

  const nativeProps = useMemo(() =>
      getProcessedColors({
        ...props,
        onRouteFound,
        onCameraPositionReceived,
        onVisibleRegionReceived,
        onWorldToScreenPointsReceived,
        onScreenToWorldPointsReceived,
        showUserPosition,
        userLocationIcon: getImageUri(props.userLocationIcon),
      }, ['userLocationAccuracyFillColor', 'userLocationAccuracyStrokeColor']),
    [props, showUserPosition]
  );

  return (
    <YamapNativeComponent
      {...nativeProps}
      ref={mapRef}
    />
  );
});
