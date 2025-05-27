import codegenNativeCommands from 'react-native/Libraries/Utilities/codegenNativeCommands';
import {Double} from 'react-native/Libraries/Types/CodegenTypes';
import {YamapComponentType} from '../YamapNativeComponent';
import {ClusteredYamapComponentType} from '../ClusteredYamapNativeComponent';
import {Animation, Point, ScreenPoint, Vehicles} from '../../interfaces';

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
  fitAllMarkers: (viewRef: React.ElementRef<YamapComponentType | ClusteredYamapComponentType>, args: Array<Readonly<{}>>) => void;
  fitMarkers: (viewRef: React.ElementRef<YamapComponentType | ClusteredYamapComponentType>, points: Array<Readonly<{points: Point[]}>>) => void;
  findRoutes: (
    viewRef: React.ElementRef<YamapComponentType | ClusteredYamapComponentType>,
    args: Array<Readonly<{
      points: Point[],
      vehicles: Vehicles[],
      id: string
    }>>
  ) => void;
  setZoom: (
    viewRef: React.ElementRef<YamapComponentType | ClusteredYamapComponentType>,
    args: Array<Readonly<{
      zoom: Double
      duration: Double,
      animation: Animation,
    }>>
  ) => void;
  getCameraPosition: (viewRef: React.ElementRef<YamapComponentType | ClusteredYamapComponentType>, args: Array<Readonly<{id: string}>>) => void;
  getVisibleRegion: (viewRef: React.ElementRef<YamapComponentType | ClusteredYamapComponentType>, args: Array<Readonly<{id: string}>>) => void;
  setTrafficVisible: (viewRef: React.ElementRef<YamapComponentType | ClusteredYamapComponentType>, args: Array<Readonly<{isVisible: boolean}>>) => void;
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

  findMasstransitRoutes: (
    viewRef: React.ElementRef<YamapComponentType | ClusteredYamapComponentType>,
    args: Array<Readonly<{
      points: Point[]
      id: string
    }>>
  ) => void;
  findPedestrianRoutes: (
    viewRef: React.ElementRef<YamapComponentType | ClusteredYamapComponentType>,
    args: Array<Readonly<{
      points: Point[]
      id: string
    }>>
  ) => void;
  findDrivingRoutes: (
    viewRef: React.ElementRef<YamapComponentType | ClusteredYamapComponentType>,
    args: Array<Readonly<{
      points: Point[]
      id: string
    }>>
  ) => void;
}

export const Commands = codegenNativeCommands<YamapNativeCommands>({
  supportedCommands: [
    'setCenter',
    'fitAllMarkers',
    'fitMarkers',
    'findRoutes',
    'setZoom',
    'getCameraPosition',
    'getVisibleRegion',
    'setTrafficVisible',
    'getScreenPoints',
    'getWorldPoints',
    'findMasstransitRoutes',
    'findPedestrianRoutes',
    'findDrivingRoutes',
  ],
});
