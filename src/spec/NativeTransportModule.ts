import {type TurboModule, TurboModuleRegistry} from 'react-native';
import type {Point, RoutesFoundState, Vehicles} from "../interfaces";

interface Spec extends TurboModule {
  findRoutes(points: Point[], vehicles: Vehicles[]): Promise<RoutesFoundState>
}

export default TurboModuleRegistry.getEnforcing<Spec>('RTNTransportModule');
