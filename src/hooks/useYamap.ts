import {type ForwardedRef, type RefObject, useImperativeHandle} from 'react';
import {Animation} from '../interfaces';
import {CallbacksManager, getImageUri} from '../utils';
import {type YamapRef} from '../components';
import {type ClusteredYamapRef} from '../components/ClusteredYamap/types';
import {type YamapNativeCommands} from '../spec/commands/yamap';
import {type YamapNativeRef} from '../spec/YamapNativeComponent';
import {type ClusteredYamapNativeRef} from '../spec/ClusteredYamapNativeComponent';

type AnyYamapNativeRef = YamapNativeRef | ClusteredYamapNativeRef;

const buildBaseHandle = <R extends AnyYamapNativeRef>(
  nativeRef: RefObject<R | null>,
  nativeCommands: YamapNativeCommands,
): YamapRef => ({
  setCenter: (center, zoom = 10, azimuth = 0, tilt = 0, duration = 0, animation = Animation.SMOOTH) =>
    nativeCommands.setCenter(nativeRef.current!, [{center, zoom, azimuth, tilt, duration, animation}]),
  fitAllMarkers: (duration = 0.7, animation = Animation.SMOOTH) =>
    nativeCommands.fitAllMarkers(nativeRef.current!, [{duration, animation}]),
  fitMarkers: (points, duration = 0.7, animation = Animation.SMOOTH) =>
    nativeCommands.fitMarkers(nativeRef.current!, [{points, duration, animation}]),
  setTrafficVisible: (isVisible) =>
    nativeCommands.setTrafficVisible(nativeRef.current!, [{isVisible}]),
  setZoom: (zoom, duration = 0, animation = Animation.SMOOTH) =>
    nativeCommands.setZoom(nativeRef.current!, [{zoom, duration, animation}]),
  getCameraPosition: (callback) => {
    const id = CallbacksManager.addCallback(callback);
    nativeCommands.getCameraPosition(nativeRef.current!, [{id}]);
  },
  getVisibleRegion: (callback) => {
    const id = CallbacksManager.addCallback(callback);
    nativeCommands.getVisibleRegion(nativeRef.current!, [{id}]);
  },
  getScreenPoints: (points, callback) => {
    const id = CallbacksManager.addCallback(callback);
    nativeCommands.getScreenPoints(nativeRef.current!, [{points, id}]);
  },
  getWorldPoints: (points, callback) => {
    const id = CallbacksManager.addCallback(callback);
    nativeCommands.getWorldPoints(nativeRef.current!, [{points, id}]);
  },
});

export const useYamap = (
  nativeRef: RefObject<YamapNativeRef | null>,
  ref: ForwardedRef<YamapRef>,
  nativeCommands: YamapNativeCommands,
) => {
  useImperativeHandle(
    ref,
    () => buildBaseHandle(nativeRef, nativeCommands),
    [nativeCommands, nativeRef],
  );
};

export const useClusteredYamap = (
  nativeRef: RefObject<ClusteredYamapNativeRef | null>,
  ref: ForwardedRef<ClusteredYamapRef>,
  nativeCommands: YamapNativeCommands,
) => {
  useImperativeHandle(ref, () => ({
    ...buildBaseHandle(nativeRef, nativeCommands),
    appendClusterMarkers: (points, options) =>
      nativeCommands.appendClusterMarkers(nativeRef.current!, [{
        points,
        iconSource: getImageUri(options?.iconSource),
        anchorX: options?.anchor?.x,
        anchorY: options?.anchor?.y,
        recluster: options?.recluster ?? true,
      }]),
    clearClusterMarkers: () =>
      nativeCommands.clearClusterMarkers(nativeRef.current!, [{}]),
  }), [nativeCommands, nativeRef]);
};
