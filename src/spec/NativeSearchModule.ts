// eslint-disable-next-line @react-native/no-deep-imports
import {type Double} from 'react-native/Libraries/Types/CodegenTypes'
import {type TurboModule, TurboModuleRegistry} from 'react-native'
import type {Address} from '../interfaces'

export interface BoundingBox {
  southWest: Point
  northEast: Point
}

export enum SearchSnippet {
  NONE = 'NONE',
  PANORAMAS = 'PANORAMAS',
  PHOTOS = 'PHOTOS',
  BUSINESSRATING1X = 'BUSINESSRATING1X', // ios only
}

export enum SearchType {
  NONE = 'NONE',
  GEO = 'GEO',
  BIZ = 'BIZ',
}

export interface Point {
  lat: Double
  lon: Double
}

export type FigureParams = {
  point?: Point
  boundingBox?: BoundingBox
  polyline?: Point[]
  polygon?: Point[]
}

export interface SearchOptions {
  disableSpellingCorrection?: boolean
  geometry?: boolean
  snippets?: SearchSnippet[]
  searchTypes?: SearchType[]
}

interface Spec extends TurboModule {
  searchByAddress(query: string, figure: FigureParams, options: SearchOptions): Promise<Address>
  searchByPoint(point: Point, zoom: Double, options: SearchOptions): Promise<Address>
  geoToAddress(point: Point): Promise<Address>
  addressToGeo(address: string): Promise<Point>
  resolveURI(query: string, options: SearchOptions): Promise<Address>
  searchByURI(query: string, options: SearchOptions): Promise<Address>
}

export default TurboModuleRegistry.getEnforcing<Spec>('RTNSearchModule')
