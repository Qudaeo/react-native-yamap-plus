import React from 'react';
import {type ImageSourcePropType, type NativeSyntheticEvent} from 'react-native';
import type {Point} from "../";
import {type OmitEx} from '../../utils';
import type {ClusterPlacemarkPress, ClusteredYamapNativeProps, YandexClusterSizes} from '../../spec/ClusteredYamapNativeComponent';
import type {YamapRef} from '../Yamap/types';

export type AppendClusterMarkersOptions = {
  /** Per-marker icon. Resolved to a URI internally. If omitted, a neutral placeholder is used. */
  iconSource?: ImageSourcePropType;
  /**
   * Icon anchor in image-relative space. (0.5, 0.5) = center, (0.5, 1) =
   * bottom-center (e.g. tail-tip of a pin). Values outside 0..1 are passed
   * through to Yandex MapKit. Applied as the `YMKIconStyle` anchor for every
   * placemark in this append batch. Defaults to Yandex's built-in (0.5, 0.5)
   * when omitted.
   */
  anchor?: { x: number; y: number };
  /**
   * Whether to run the clustering pass after adding points. Defaults to `true`.
   * Pass `false` for intermediate batches when streaming many appends and call
   * once more with the default for the final batch to avoid O(total) clustering
   * work on every append.
   */
  recluster?: boolean;
};

export type ClusteredYamapRef = YamapRef & {
  /** Append points to the cluster collection without clearing existing ones. */
  appendClusterMarkers: (points: Point[], options?: AppendClusterMarkersOptions) => void;
  /** Remove all imperatively-added points from the cluster collection. */
  clearClusterMarkers: () => void;
};

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
  'onScreenToWorldPointsReceived' |
  'onClusterPlacemarkPress'
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
  /**
   * Fires when the user taps a placemark added via
   * `ref.current.appendClusterMarkers(...)`. Does not fire for clusters of
   * size ≥ 2 (those go to the cluster collapse handler) and does not fire
   * for `<Marker>` children. `index` is the zero-based append-order index
   * across all `appendClusterMarkers` batches; it resets after
   * `clearClusterMarkers()`.
   */
  onClusterPlacemarkPress?: (event: NativeSyntheticEvent<ClusterPlacemarkPress>) => void
}
