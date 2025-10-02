import React, { FC, useMemo } from 'react';
import {OmitEx, processColorsToNative} from '../utils';
import PolygonNativeComponent, {PolygonNativeProps} from '../spec/PolygonNativeComponent';

type PolygonProps = OmitEx<PolygonNativeProps, 'fillColor' | 'strokeColor' | 'zI'> & {
  fillColor?: string;
  strokeColor?: string;
  zIndex?: number;
}

export const Polygon: FC<PolygonProps> = ({zIndex, ...props}) => {
  const nativeProps = useMemo(() =>
    processColorsToNative(props, ['fillColor', 'strokeColor']),
    [props]
  );

  return <PolygonNativeComponent
    zI={zIndex}
    {...nativeProps}
  />;
};
