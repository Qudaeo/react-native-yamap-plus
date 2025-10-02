import React, {FC, useMemo} from 'react';
import {OmitEx, processColorsToNative} from '../utils';
import CircleNativeComponent, {CircleNativeProps} from '../spec/CircleNativeComponent';

type CircleProps = OmitEx<CircleNativeProps, 'fillColor' | 'strokeColor' | 'zI'> & {
  fillColor?: string;
  strokeColor?: string;
  zIndex?: number;
}

export const Circle: FC<CircleProps> = ({zIndex, ...props}) => {
  const nativeProps = useMemo(() =>
    processColorsToNative(props, ['fillColor', 'strokeColor']),
    [props]
  );

  return <CircleNativeComponent
    zI={zIndex}
    {...nativeProps}
  />;
};
