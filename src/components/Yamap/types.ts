import {ImageSourcePropType} from 'react-native';
import {
  Animation,
  CameraPositionCallback,
  Point,
  ScreenPoint,
  ScreenPointsCallback,
  VisibleRegionCallback,
  WorldPointsCallback,
} from '../../interfaces';
import {OmitEx} from '../../utils';
import {YamapNativeProps} from '../../spec/YamapNativeComponent';

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
};
