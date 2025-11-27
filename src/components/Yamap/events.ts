import {type NativeSyntheticEvent} from "react-native";
import type {Point, ScreenPoint, VisibleRegion} from '../../interfaces';
import {CallbacksManager} from '../../utils';
import {type CameraPosition} from '../../spec/YamapNativeComponent';

export const onCameraPositionReceived = (event: NativeSyntheticEvent<{ id: string } & CameraPosition>) => {
  const { id, ...point } = event.nativeEvent;
  CallbacksManager.call(id, point);
};

export const onVisibleRegionReceived = (event: NativeSyntheticEvent<{ id: string } & VisibleRegion>) => {
  const { id, ...visibleRegion } = event.nativeEvent;
  CallbacksManager.call(id, visibleRegion);
};

export const onWorldToScreenPointsReceived = (event: NativeSyntheticEvent<{ id: string } & ScreenPoint[]>) => {
  const { id, ...screenPoints } = event.nativeEvent;
  CallbacksManager.call(id, screenPoints);
};

export const onScreenToWorldPointsReceived = (event: NativeSyntheticEvent<{ id: string } & Point[]>) => {
  const { id, ...worldPoints } = event.nativeEvent;
  CallbacksManager.call(id, worldPoints);
};
