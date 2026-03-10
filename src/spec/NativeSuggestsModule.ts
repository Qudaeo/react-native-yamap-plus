// eslint-disable-next-line @react-native/no-deep-imports
import {type Double} from 'react-native/Libraries/Types/CodegenTypes';
import {type TurboModule, TurboModuleRegistry} from 'react-native';

interface Point {
  lat: Double;
  lon: Double;
}

export type YamapSuggest = {
  title: string;
  subtitle?: string;
  uri?: string;
  center?: Point;
}

export enum SuggestType {
  UNSPECIFIED = 'UNSPECIFIED',
  GEO = 'GEO',
  BIZ = 'BIZ',
  TRANSIT = 'TRANSIT',
}

interface BoundingBox {
  southWest: Point;
  northEast: Point;
}

export interface SuggestOptions {
  userPosition?: Point;
  boundingBox?: BoundingBox;
  suggestWords?: boolean;
  suggestTypes?: SuggestType[];
}

interface Spec extends TurboModule {
  suggest(query: string): Promise<Array<YamapSuggest>>
  suggestWithOptions(query: string, options: SuggestOptions): Promise<Array<YamapSuggest>>
  reset(): Promise<void>
}

export default TurboModuleRegistry.getEnforcing<Spec>('RTNSuggestsModule');
