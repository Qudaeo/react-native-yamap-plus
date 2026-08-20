// eslint-disable-next-line @react-native/no-deep-imports
import codegenNativeCommands from 'react-native/Libraries/Utilities/codegenNativeCommands';
// eslint-disable-next-line @react-native/no-deep-imports
import {type Double} from 'react-native/Libraries/Types/CodegenTypes';
import {type YamapNativeRef} from '../YamapNativeComponent';
import {Animation, type ScreenPoint} from '../../interfaces';
import type {Point} from "../../";

export interface YamapNativeCommands<T = YamapNativeRef> {
  setCenter: (
    viewRef: T,
    args: Array<Readonly<{
      duration: Double;
      center: Point,
      zoom: Double;
      azimuth: Double;
      tilt: Double;
      animation: Animation
    }>>,
  ) => void;
  fitAllMarkers: (
    viewRef: T,
    args: Array<Readonly<{
      duration?: number,
      animation?: Animation,
    }>>) => void;
  fitMarkers: (
    viewRef: T,
    args: Array<Readonly<{
      points: Point[],
      duration?: number,
      animation?: Animation,
    }>>) => void;
  setZoom: (
    viewRef: T,
    args: Array<Readonly<{
      zoom: Double
      duration: Double,
      animation: Animation,
    }>>
  ) => void;
  getCameraPosition: (
    viewRef: T,
    args: Array<Readonly<{
      id: string,
    }>>) => void;
  getVisibleRegion: (
    viewRef: T,
    args: Array<Readonly<{
      id: string,
    }>>) => void;
  setTrafficVisible: (
    viewRef: T,
    args: Array<Readonly<{
      isVisible: boolean,
    }>>) => void;
  getScreenPoints: (
    viewRef: T,
    args: Array<Readonly<{
      points: Point[]
      id: string
    }>>
  ) => void;
  getWorldPoints: (
    viewRef: T,
    args: Array<Readonly<{
      points: ScreenPoint[]
      id: string
    }>>
  ) => void;
  appendClusterMarkers: (
    viewRef: T,
    args: Array<Readonly<{
      points: Point[],
      iconSource?: string,
      anchorX?: Double,
      anchorY?: Double,
      recluster?: boolean,
    }>>
  ) => void;
  clearClusterMarkers: (
    viewRef: T,
    args: Array<Readonly<Record<string, never>>>
  ) => void;
}

export const Commands = codegenNativeCommands<YamapNativeCommands>({
  supportedCommands: [
    'setCenter',
    'fitAllMarkers',
    'fitMarkers',
    'setZoom',
    'getCameraPosition',
    'getVisibleRegion',
    'setTrafficVisible',
    'getScreenPoints',
    'getWorldPoints',
    'appendClusterMarkers',
    'clearClusterMarkers',
  ],
});
