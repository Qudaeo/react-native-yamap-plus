import React, {forwardRef, useImperativeHandle, useMemo, useRef} from 'react';
import {ImageSourcePropType} from 'react-native';
import {getImageUri, OmitEx} from '../utils';
import MarkerNativeComponent, {MarkerNativeProps} from '../spec/MarkerNativeComponent';
import {Commands} from '../spec/commands/marker';
import {Point} from '../interfaces';

export type MarkerProps = OmitEx<MarkerNativeProps, 'source' | 'zI'> &  {
  source?: ImageSourcePropType;
  zIndex?: number;
}

export interface MarkerRef {
  animatedMoveTo: (coords: Point, duration: number) => void;
  animatedRotateTo: (angle: number, duration: number) => void;
}

export const Marker = forwardRef<MarkerRef, MarkerProps>(({source, zIndex, ...props}, ref) => {
  const nativeRef = useRef(null);

  const imageUri = useMemo(() => getImageUri(source), [source]);

  useImperativeHandle<MarkerRef, any>(ref, () => ({
    animatedMoveTo: (coords: Point, duration: number) =>
      Commands.animatedMoveTo(nativeRef.current!, [{coords, duration}]),
    animatedRotateTo: (angle: number, duration: number) =>
      Commands.animatedRotateTo(nativeRef.current!, [{angle, duration}]),
  }), []);

  return (
    <MarkerNativeComponent
      zI={zIndex}
      {...props}
      ref={nativeRef}
      source={imageUri}
      pointerEvents="none"
    />
  );
});
