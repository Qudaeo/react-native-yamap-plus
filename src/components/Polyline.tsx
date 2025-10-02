import React, {FC, useMemo} from 'react';
import {OmitEx, processColorsToNative} from '../utils';
import PolylineNativeComponent, {PolylineNativeProps} from '../spec/PolylineNativeComponent';

type PolylineProps = OmitEx<PolylineNativeProps, 'strokeColor' | 'outlineColor' | 'zI'> & {
  strokeColor?: string;
  outlineColor?: string;
  zIndex?: number;
}

export const Polyline: FC<PolylineProps> = ({zIndex, ...props}) => {
  const nativeProps = useMemo(() =>
    processColorsToNative(props, ['strokeColor', 'outlineColor']),
    [props]
  );

  return <PolylineNativeComponent
    zI={zIndex}
    {...nativeProps}
  />;
};
