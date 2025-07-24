import {ForwardedRef, RefObject, useImperativeHandle} from 'react';
import {Animation} from '../interfaces';
import {CallbacksManager} from '../utils';
import {YamapRef} from '../components';
import {YamapNativeCommands} from '../spec/commands/yamap';
import {YamapNativeRef} from '../spec/YamapNativeComponent';

export const useYamap = (
  nativeRef: RefObject<YamapNativeRef | null>,
  ref: ForwardedRef<YamapRef>,
  nativeCommands: YamapNativeCommands,
) => {
  useImperativeHandle(ref, () => ({
    setCenter: (center, zoom = 10, azimuth = 0, tilt = 0, duration = 0, animation = Animation.SMOOTH) =>
      nativeCommands.setCenter(nativeRef.current!, [{center, zoom, azimuth, tilt, duration, animation}]),
    fitAllMarkers: () => nativeCommands.fitAllMarkers(nativeRef.current!, [{}]),
    fitMarkers: (points) => nativeCommands.fitMarkers(nativeRef.current!, [{points}]),
    setTrafficVisible: (isVisible) => nativeCommands.setTrafficVisible(nativeRef.current!, [{isVisible}]),
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
  }), [nativeCommands, nativeRef]);
};
