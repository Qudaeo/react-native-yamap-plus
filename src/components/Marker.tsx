// eslint-disable-next-line @typescript-eslint/consistent-type-imports
import React, { forwardRef, useCallback, useEffect, useImperativeHandle, useMemo, useRef } from 'react';
import { Image, type ImageSourcePropType, Platform, UIManager, findNodeHandle, requireNativeComponent } from 'react-native';

import type { Anchor, Point } from '../interfaces';
import type { OmitEx } from '../utils/types';

const COMPONENT_NAME = 'YamapMarker';

export interface MarkerProps {
  children?: React.ReactElement;
  zIndex?: number;
  scale?: number;
  rotated?: boolean;
  point: Point;
  source?: ImageSourcePropType;
  anchor?: Anchor;
  visible?: boolean;

  onPress?: () => void;
}

export interface MarkerRef {
  animatedMoveTo: AnimatedMoveToFunc;
  animatedRotateTo: AnimatedRotateToFunc;
}

type AnimatedMoveToFunc = (coords: Point, duration: number) => void;
type AnimatedRotateToFunc = (angle: number, duration: number) => void;

type MarkerNativeComponentProps = OmitEx<MarkerProps, 'source'> & {
  source?: string;
};

const NativeMarkerComponent = requireNativeComponent<MarkerNativeComponentProps>(COMPONENT_NAME);

const getCommand = (cmd: string) => {
  return Platform.OS === 'ios' ? UIManager.getViewManagerConfig(COMPONENT_NAME).Commands[cmd] : cmd;
};

export const Marker = forwardRef<MarkerRef, MarkerProps>(({
  source,
  visible = true,
  rotated = false,
  ...props
}, ref) => {
  const nativeRef = useRef(null);

  const resolvedSource = useMemo(() => source ? Image.resolveAssetSource(source) : undefined, [source]);

  const animatedMoveTo = useCallback<AnimatedMoveToFunc>((coords, duration) => {
    UIManager.dispatchViewManagerCommand(findNodeHandle(nativeRef.current), getCommand('animatedMoveTo'), [coords, duration]);
  }, []);

  const animatedRotateTo = useCallback<AnimatedRotateToFunc>((angle, duration) => {
    UIManager.dispatchViewManagerCommand(findNodeHandle(nativeRef.current), getCommand('animatedRotateTo'), [angle, duration]);
  }, []);

  useImperativeHandle(ref, () => ({
    animatedMoveTo,
    animatedRotateTo
  }), [animatedMoveTo, animatedRotateTo]);

  useEffect(() => {
    console.log('Marker rendered');
  }, []);

  return (
    <NativeMarkerComponent
      {...props}
      ref={nativeRef}
      source={resolvedSource?.uri}
      visible={visible}
      rotated={rotated}
    />
  );
});

//   state = {
//     recreateKey: false,
//     children: this.props.children
//   };

//   static getDerivedStateFromProps(nextProps: MarkerProps, prevState: State): Partial<State> {
//     if (Platform.OS === 'ios') {
//       return {
//         children: nextProps.children,
//         recreateKey:
//           nextProps.children === prevState.children
//             ? prevState.recreateKey
//             : !prevState.recreateKey
//       };
//     }

//     return {
//       children: nextProps.children,
//       recreateKey: Boolean(nextProps.children)
//     };
//   }

//   render() {
//     return (
//       <NativeMarkerComponent
//         {...this.getProps()}
//         key={String(this.state.recreateKey)}
//         pointerEvents="none"
//       />
//     );
//   }
