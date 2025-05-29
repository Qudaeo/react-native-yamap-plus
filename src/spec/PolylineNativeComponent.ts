import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';
import {BubblingEventHandler, Double} from 'react-native/Libraries/Types/CodegenTypes';
import {ViewProps} from 'react-native';

interface Point {
  lat: Double;
  lon: Double;
}

export interface PolylineNativeProps extends ViewProps {
  strokeColor?: Double;
  outlineColor?: Double;
  strokeWidth?: Double;
  outlineWidth?: Double;
  dashLength?: Double;
  dashOffset?: Double;
  gapLength?: Double;
  onPress?: BubblingEventHandler<undefined>;
  points: Point[];
  handled?: boolean;
}

export default codegenNativeComponent<PolylineNativeProps>('YamapPolyline');
