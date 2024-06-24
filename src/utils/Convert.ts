import type { ClusterMarker } from '../interfaces';

export function convertClusterMarkers<T = any>(items: ReadonlyArray<ClusterMarker<T>>): number[] {
  const result: number[] = [];

  for (const item of items) {
    result.push(item.point.lat, item.point.lon);
  }

  return result;
}
