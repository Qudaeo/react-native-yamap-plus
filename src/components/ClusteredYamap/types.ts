import type { Component, RefObject } from 'react';
import { type NativeMethods, type ProcessedColorValue } from 'react-native';

import type { ClusterMarker, ClusterRenderFunc } from '../../interfaces';
import type { YamapNativeComponentProps, YamapProps, YamapRef } from '../Yamap/types';

export interface ClusteredYamapProps extends YamapProps {
  clusteredMarkers: ReadonlyArray<ClusterMarker>;
  renderMarker: ClusterRenderFunc;
  clusterColor?: string;
}

export type ClusteredYamapNativeComponentProps = YamapNativeComponentProps & {
  clusteredMarkers: ReadonlyArray<number>;
  clusterColor?: ProcessedColorValue | null | undefined;
};

export type ClusteredYamapNativeRef = Component<ClusteredYamapNativeComponentProps, object, any> & Readonly<NativeMethods>;

export type ClusteredYamapRef = Omit<YamapRef, 'nativeRef'> & {
  nativeRef: RefObject<ClusteredYamapNativeRef>;
};
