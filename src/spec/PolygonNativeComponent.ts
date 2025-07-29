import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';
import {
  BubblingEventHandler,
  Double,
  Float,
  Int32,
} from 'react-native/Libraries/Types/CodegenTypes';
import {ViewProps} from 'react-native';

interface Point {
  lat: Double;
  lon: Double;
}

export interface PolygonNativeProps extends ViewProps {
  fillColor?: Int32;
  strokeColor?: Int32;
  strokeWidth?: Float;
  onPress?: BubblingEventHandler<undefined>;
  points: Point[];
  innerRings?: (Point[])[];
  handled?: boolean;
  zI?: Float;
}

export default codegenNativeComponent<PolygonNativeProps>('PolygonView');
