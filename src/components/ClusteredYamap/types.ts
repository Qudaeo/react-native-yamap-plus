import React from 'react';
import {type ImageSourcePropType} from 'react-native';
import type {Point} from "../";
import {type OmitEx} from '../../utils';
import type {ClusteredYamapNativeProps, YandexClusterSizes} from '../../spec/ClusteredYamapNativeComponent';

export type ClusteredYamapProps<T = any> = OmitEx<ClusteredYamapNativeProps,
  'userLocationAccuracyFillColor' |
  'userLocationAccuracyStrokeColor' |
  'clusterColor' |
  'userLocationIcon' |
  'clusteredMarkers' |
  'clusterIcon' |
  'clusterTextColor' |
  'onCameraPositionReceived' |
  'onVisibleRegionReceived' |
  'onWorldToScreenPointsReceived' |
  'onScreenToWorldPointsReceived'
> & {
  clusterColor?: string;
  userLocationAccuracyFillColor?: string;
  userLocationAccuracyStrokeColor?: string;
  userLocationIcon?: ImageSourcePropType;
  /**
   * Prop-based cluster markers. Optional — you can also drive the cluster
   * collection imperatively via `ref.current.appendClusterMarkers(...)`.
   */
  clusteredMarkers?: ReadonlyArray<{point: Point, data: T}>
  clusterIcon?: ImageSourcePropType;
  clusterSize?: YandexClusterSizes;
  clusterTextSize?: number;
  clusterTextYOffset?: number;
  clusterTextXOffset?: number;
  clusterTextColor?: string;
  /** Required only when `clusteredMarkers` is provided. */
  renderMarker?: (info: {point: Point, data: T}, index: number) => React.ReactElement
}
