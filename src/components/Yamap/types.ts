import {type ImageSourcePropType} from 'react-native';
import type {
  Animation,
  CameraPositionCallback,
  ScreenPoint,
  ScreenPointsCallback,
  VisibleRegionCallback,
  WorldPointsCallback,
} from '../../interfaces';
import {type OmitEx} from '../../utils';
import {type YamapNativeProps} from '../../spec/YamapNativeComponent';
import type {Point} from "../";

export type YamapProps = OmitEx<YamapNativeProps,
  'userLocationAccuracyFillColor' |
  'userLocationAccuracyStrokeColor' |
  'userLocationIcon' |
  'onCameraPositionReceived' |
  'onVisibleRegionReceived' |
  'onWorldToScreenPointsReceived' |
  'onScreenToWorldPointsReceived'
> & {
  userLocationAccuracyFillColor?: string;
  userLocationAccuracyStrokeColor?: string;
  userLocationIcon?: ImageSourcePropType;
}

export type YamapRef = {
  setCenter: (
    center: Point,
    zoom?: number,
    azimuth?: number,
    tilt?: number,
    duration?: number,
    animation?: Animation
  ) => void;
  fitAllMarkers: (duration?: number, animation?: Animation) => void;
  fitMarkers: (points: Point[], duration?: number, animation?: Animation) => void;
  setZoom: (zoom: number, duration?: number, animation?: Animation) => void;
  getCameraPosition: (callback: CameraPositionCallback) => void;
  getVisibleRegion: (callback: VisibleRegionCallback) => void;
  setTrafficVisible: (isVisible: boolean) => void;
  getScreenPoints: (points: Point[], callback: ScreenPointsCallback) => void;
  getWorldPoints: (points: ScreenPoint[], callback: WorldPointsCallback) => void;
  /**
   * Append points to the cluster collection without clearing existing ones.
   * Only takes effect on ClusteredYamap. Pass an ImageSourcePropType via
   * iconSource to set the per-marker icon (resolved to a URI internally); if
   * omitted, the library falls back to the cluster-style icon.
   */
  appendClusterMarkers: (points: Point[], iconSource?: ImageSourcePropType) => void;
  /** Remove all points from the cluster collection. */
  clearClusterMarkers: () => void;
};
