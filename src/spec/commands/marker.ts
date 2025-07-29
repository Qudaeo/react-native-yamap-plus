import {Double, Float} from 'react-native/Libraries/Types/CodegenTypes';
import codegenNativeCommands from 'react-native/Libraries/Utilities/codegenNativeCommands';
import {MarkerComponentType} from '../MarkerNativeComponent';
import {Point} from '../../interfaces';

export interface MarkerNativeCommands {
  animatedMoveTo: (
    viewRef: React.ElementRef<MarkerComponentType>,
    args: Array<Readonly<{coords: Point, duration: Double}>>
  ) => void;
  animatedRotateTo: (
    viewRef: React.ElementRef<MarkerComponentType>,
    args: Array<Readonly<{angle: Float, duration: Double}>>
  ) => void;
}

export const Commands = codegenNativeCommands<MarkerNativeCommands>({
  supportedCommands: [
    'animatedMoveTo',
    'animatedRotateTo',
  ],
});
