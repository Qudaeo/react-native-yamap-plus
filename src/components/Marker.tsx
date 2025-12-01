import React, {forwardRef, useImperativeHandle, useMemo, useRef} from 'react';
import {type ImageSourcePropType} from 'react-native';
import {getImageUri, type OmitEx} from '../utils';
import MarkerNativeComponent, {type MarkerNativeProps} from '../spec/MarkerNativeComponent';
import {Commands} from '../spec/commands/marker';
import {type Point} from '../interfaces';

export type MarkerProps = OmitEx<MarkerNativeProps, 'source' | 'zI'> &  {
  source?: ImageSourcePropType;
  zIndex?: number;
}

export interface MarkerRef {
  animatedMoveTo: (coords: Point, duration: number) => void;
  animatedRotateTo: (angle: number, duration: number) => void;
  updateMarker: () => void;
}

export const Marker = forwardRef<MarkerRef, MarkerProps>(({ source, zIndex, visible = true, ...props }, ref) => {
  const nativeRef = useRef(null);

  const imageUri = useMemo(() => getImageUri(source), [source]);

  useImperativeHandle<MarkerRef, any>(ref, () => ({
    animatedMoveTo: (coords: Point, duration: number) =>
      Commands.animatedMoveTo(nativeRef.current!, [{ coords, duration }]),
    animatedRotateTo: (angle: number, duration: number) =>
      Commands.animatedRotateTo(nativeRef.current!, [{ angle, duration }]),
    updateMarker: () =>
      Commands.updateMarker(nativeRef.current!),
  }), []);

  return (
    <MarkerNativeComponent
      zI={zIndex}
      {...props}
      ref={nativeRef}
      source={imageUri}
      visible={visible}
      pointerEvents="none"
    />
  );
});
