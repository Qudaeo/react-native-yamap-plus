import React, { forwardRef, useImperativeHandle, useMemo, useRef } from 'react';
import { processColor, requireNativeComponent } from 'react-native';

import { useYamap } from '../../hooks/useYamap';
import { convertClusterMarkers } from '../../utils/Convert';

import type { ClusteredYamapNativeComponentProps, ClusteredYamapNativeRef, ClusteredYamapProps, ClusteredYamapRef } from './types';

const COMPONENT_NAME = 'ClusteredYamapView';

const ClusteredYamapNativeComponent = requireNativeComponent<ClusteredYamapNativeComponentProps>(COMPONENT_NAME);

export const ClusteredYamap = forwardRef<ClusteredYamapRef, ClusteredYamapProps>(({
  showUserPosition = true,
  userLocationIcon,
  userLocationAccuracyFillColor,
  userLocationAccuracyStrokeColor,

  clusterColor = 'red',
  clusteredMarkers,
  renderMarker,
  ...props
}, ref) => {
  const nativeRef = useRef<ClusteredYamapNativeRef>(null);

  const processedColor = useMemo(() => processColor(clusterColor), [clusterColor]);
  const points = useMemo(() => convertClusterMarkers(clusteredMarkers), [clusteredMarkers]);
  const children = useMemo(() => clusteredMarkers.map((t, i) => renderMarker(t, i)), [clusteredMarkers, renderMarker]);

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
    <ClusteredYamapNativeComponent
      {...props}

      ref={nativeRef}

      children={children}
      clusteredMarkers={points}
      clusterColor={processedColor}

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
