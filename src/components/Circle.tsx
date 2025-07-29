import React, {FC, useMemo} from 'react';
import {OmitEx, processColorsToNative} from '../utils';
import CircleNativeComponent, {CircleNativeProps} from '../spec/CircleNativeComponent';

type CircleProps = OmitEx<CircleNativeProps, 'fillColor' | 'strokeColor'> & {
  fillColor?: string;
  strokeColor?: string;
}

export const Circle: FC<CircleProps> = (props) => {
  const nativeProps = useMemo(() =>
    processColorsToNative(props, ['fillColor', 'strokeColor']),
    [props]
  );

  return <CircleNativeComponent {...nativeProps} />;
};
