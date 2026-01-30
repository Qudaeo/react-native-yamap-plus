import {type TurboModule, TurboModuleRegistry} from 'react-native';
import type {RoutesFoundState, Vehicles} from "../interfaces";
import type {Point} from "../";

interface Spec extends TurboModule {
  findRoutes(points: Point[], vehicles: Vehicles[]): Promise<RoutesFoundState>
}

export default TurboModuleRegistry.getEnforcing<Spec>('RTNTransportModule');
