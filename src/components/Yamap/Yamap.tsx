import React, { forwardRef, useImperativeHandle, useRef } from 'react';
import { requireNativeComponent } from 'react-native';

import { useYamap } from '../../hooks/useYamap';

import { type YamapNativeComponentProps, type YamapNativeRef, type YamapProps, type YamapRef } from './types';

const COMPONENT_NAME = 'YamapView';

const YamapNativeComponent = requireNativeComponent<YamapNativeComponentProps>(COMPONENT_NAME);

export const Yamap = forwardRef<YamapRef, YamapProps>(({
  showUserPosition = true,
  userLocationIcon,
  userLocationAccuracyFillColor,
  userLocationAccuracyStrokeColor,
  ...props
}: YamapProps, ref) => {
  const nativeRef = useRef<YamapNativeRef>(null);

  const {
    resolvedUserLocationIcon,
    processedUserLocationAccuracyFillColor,
    processedUserLocationAccuracyStrokeColor,

    onRouteFound,
    onCameraPositionReceived,
    onVisibleRegionReceived,
    onWorldToScreenPointsReceived,
    onScreenToWorldPointsReceived,

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
  } = useYamap(COMPONENT_NAME, nativeRef, {
    userLocationIcon,
    userLocationAccuracyFillColor,
    userLocationAccuracyStrokeColor,
  });

  useImperativeHandle(ref, () => ({
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
    nativeRef: nativeRef,
  }), [
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
    findDrivingRoutes
  ]);

  return (
    <YamapNativeComponent
      {...props}

      ref={nativeRef}

      showUserPosition={showUserPosition}
      userLocationIcon={resolvedUserLocationIcon?.uri}
      userLocationAccuracyFillColor={processedUserLocationAccuracyFillColor}
      userLocationAccuracyStrokeColor={processedUserLocationAccuracyStrokeColor}

      onRouteFound={onRouteFound}
      onCameraPositionReceived={onCameraPositionReceived}
      onVisibleRegionReceived={onVisibleRegionReceived}
      onWorldToScreenPointsReceived={onWorldToScreenPointsReceived}
      onScreenToWorldPointsReceived={onScreenToWorldPointsReceived}
    />
  );
});
