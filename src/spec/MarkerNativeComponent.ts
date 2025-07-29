import codegenNativeComponent, {NativeComponentType} from 'react-native/Libraries/Utilities/codegenNativeComponent';
import {BubblingEventHandler, Double, Float} from 'react-native/Libraries/Types/CodegenTypes';
import {ViewProps} from 'react-native';
import {MarkerNativeCommands} from './commands/marker';

interface Point {
  lat: Double;
  lon: Double;
}

type Anchor = {
  x: Double;
  y: Double;
}

export interface MarkerNativeProps extends ViewProps {
  scale?: Float;
  rotated?: boolean;
  onPress?: BubblingEventHandler<undefined>;
  point: Point;
  anchor?: Anchor;
  visible?: boolean;
  handled?: boolean;
  source?: string;
  zI?: Float;
}

export type MarkerComponentType = NativeComponentType<MarkerNativeProps> & Readonly<MarkerNativeCommands>;

require('./commands/marker');

export default codegenNativeComponent<MarkerNativeProps>('MarkerView');
