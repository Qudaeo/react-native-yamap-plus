import React, { FC, useMemo } from 'react';
import {OmitEx, processColorsToNative} from '../utils';
import PolygonNativeComponent, {PolygonNativeProps} from '../spec/PolygonNativeComponent';

type PolygonProps = OmitEx<PolygonNativeProps, 'fillColor' | 'strokeColor'> & {
  fillColor?: string;
  strokeColor?: string;
}

export const Polygon: FC<PolygonProps> = (props) => {
  const nativeProps = useMemo(() =>
    processColorsToNative(props, ['fillColor', 'strokeColor']),
    [props]
  );

  return <PolygonNativeComponent {...nativeProps} />;
};
