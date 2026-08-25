import type {ComponentRef} from 'react';
import {codegenNativeCommands, type CodegenTypes} from 'react-native';
import {type MarkerComponentType} from '../MarkerNativeComponent';
import {type Point} from "../../";

export interface MarkerNativeCommands {
  animatedMoveTo: (
    viewRef: ComponentRef<MarkerComponentType>,
    args: Array<Readonly<{coords: Point, duration: CodegenTypes.Double}>>
  ) => void;
  animatedRotateTo: (
    viewRef: ComponentRef<MarkerComponentType>,
    args: Array<Readonly<{angle: CodegenTypes.Float, duration: CodegenTypes.Double}>>
  ) => void;
}

export const Commands = codegenNativeCommands<MarkerNativeCommands>({
  supportedCommands: [
    'animatedMoveTo',
    'animatedRotateTo',
  ],
});
