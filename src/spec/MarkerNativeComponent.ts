import {codegenNativeComponent, type CodegenTypes, type HostComponent, type ViewProps} from 'react-native';
import {type MarkerNativeCommands} from './commands/marker';

interface Point {
  lat: CodegenTypes.Double;
  lon: CodegenTypes.Double;
}

type Anchor = {
  x: CodegenTypes.Double;
  y: CodegenTypes.Double;
}

export interface MarkerNativeProps extends ViewProps {
  scale?: CodegenTypes.Float;
  rotated?: boolean;
  onPress?: CodegenTypes.BubblingEventHandler<undefined>;
  point: Point;
  anchor?: Anchor;
  visible?: boolean;
  handled?: boolean;
  source?: string;
  zI?: CodegenTypes.Float;
  /**
   * Only meaningful when this Marker is a child of ClusteredYamap.
   * When true, the marker is added to the map's root object collection
   * instead of the cluster collection — it stays visible on every zoom
   * level and never merges into a cluster. Read once at mount; toggling
   * at runtime has no effect.
   */
  excludeFromCluster?: boolean;
}

export type MarkerComponentType = HostComponent<MarkerNativeProps> & Readonly<MarkerNativeCommands>;

require('./commands/marker');

export default codegenNativeComponent<MarkerNativeProps>('MarkerView');
