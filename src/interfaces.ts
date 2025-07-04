import {CameraPosition, RoutesFoundState} from './spec/YamapNativeComponent';

export interface Point {
  lat: number;
  lon: number;
}

export interface BoundingBox {
  southWest: Point;
  northEast: Point;
}

export type WorldPointsCallback = (result: {worldPoints: Point[]}) => void

export interface ScreenPoint {
  x: number;
  y: number;
}

export type ScreenPointsCallback = (result: {screenPoints: ScreenPoint[]}) => void

export type MasstransitVehicles = 'bus' | 'trolleybus' | 'tramway' | 'minibus' | 'suburban' | 'underground' | 'ferry' | 'cable' | 'funicular';

export type Vehicles = MasstransitVehicles | 'walk' | 'car';

export const ALL_MASSTRANSIT_VEHICLES: Vehicles[] = [
  'bus',
  'trolleybus',
  'tramway',
  'minibus',
  'suburban',
  'underground',
  'ferry',
  'cable',
  'funicular',
] as const;

export interface RoutesFoundEvent {
  nativeEvent: RoutesFoundState;
}

export type CameraPositionCallback = (position: CameraPosition) => void

export type RoutesFoundCallback = (event: RoutesFoundEvent) => void

export type VisibleRegion = {
  bottomLeft: Point;
  bottomRight: Point;
  topLeft: Point;
  topRight: Point;
}

export type VisibleRegionCallback = (visibleRegion: VisibleRegion) => void

export enum Animation {
  SMOOTH,
  LINEAR
}

export enum AddressKind {
  UNKNOWN,
  COUNTRY,
  REGION,
  PROVINCE,
  AREA,
  LOCALITY,
  DISTRICT,
  STREET,
  HOUSE,
  ENTRANCE,
  LEVEL,
  APARTMENT,
  ROUTE,
  STATION,
  METRO_STATION,
  RAILWAY_STATION,
  VEGETATION,
  HYDRO,
  AIRPORT,
  OTHER,
}

export interface Address {
  Components: Array<{name: string; kind: AddressKind}>
  country_code: string
  formatted: string
  point: Point
  uri: string
}
