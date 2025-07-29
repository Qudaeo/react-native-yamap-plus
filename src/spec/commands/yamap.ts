import codegenNativeCommands from 'react-native/Libraries/Utilities/codegenNativeCommands';
import {Double} from 'react-native/Libraries/Types/CodegenTypes';
import {YamapComponentType} from '../YamapNativeComponent';
import {ClusteredYamapComponentType} from '../ClusteredYamapNativeComponent';
import {Animation, Point, ScreenPoint} from '../../interfaces';

export interface YamapNativeCommands {
  setCenter: (
    viewRef: React.ElementRef<YamapComponentType | ClusteredYamapComponentType>,
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
    viewRef: React.ElementRef<YamapComponentType | ClusteredYamapComponentType>,
    args: Array<Readonly<{
      duration?: number,
      animation?: Animation,
    }>>) => void;
  fitMarkers: (
    viewRef: React.ElementRef<YamapComponentType | ClusteredYamapComponentType>,
    args: Array<Readonly<{
      points: Point[],
      duration?: number,
      animation?: Animation,
    }>>) => void;
  setZoom: (
    viewRef: React.ElementRef<YamapComponentType | ClusteredYamapComponentType>,
    args: Array<Readonly<{
      zoom: Double
      duration: Double,
      animation: Animation,
    }>>
  ) => void;
  getCameraPosition: (
    viewRef: React.ElementRef<YamapComponentType | ClusteredYamapComponentType>,
    args: Array<Readonly<{
      id: string,
    }>>) => void;
  getVisibleRegion: (
    viewRef: React.ElementRef<YamapComponentType | ClusteredYamapComponentType>,
    args: Array<Readonly<{
      id: string,
    }>>) => void;
  setTrafficVisible: (
    viewRef: React.ElementRef<YamapComponentType | ClusteredYamapComponentType>,
    args: Array<Readonly<{
      isVisible: boolean,
    }>>) => void;
  getScreenPoints: (
    viewRef: React.ElementRef<YamapComponentType | ClusteredYamapComponentType>,
    args: Array<Readonly<{
      points: Point[]
      id: string
    }>>
  ) => void;
  getWorldPoints: (
    viewRef: React.ElementRef<YamapComponentType | ClusteredYamapComponentType>,
    args: Array<Readonly<{
      points: ScreenPoint[]
      id: string
    }>>
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
  ],
});
