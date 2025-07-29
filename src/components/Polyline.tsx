import React, {FC, useMemo} from 'react';
import {OmitEx, processColorsToNative} from '../utils';
import PolylineNativeComponent, {PolylineNativeProps} from '../spec/PolylineNativeComponent';

type PolylineProps = OmitEx<PolylineNativeProps, 'strokeColor' | 'outlineColor'> & {
  strokeColor?: string;
  outlineColor?: string;
}

export const Polyline: FC<PolylineProps> = (props) => {
  const nativeProps = useMemo(() =>
    processColorsToNative(props, ['strokeColor', 'outlineColor']),
    [props]
  );

  return <PolylineNativeComponent {...nativeProps} />;
};
