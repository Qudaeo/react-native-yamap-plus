import {codegenNativeComponent, type CodegenTypes, type ViewProps} from 'react-native';

interface Point {
  lat: CodegenTypes.Double;
  lon: CodegenTypes.Double;
}

export interface PolygonNativeProps extends ViewProps {
  fillColor?: CodegenTypes.Int32;
  strokeColor?: CodegenTypes.Int32;
  strokeWidth?: CodegenTypes.Float;
  onPress?: CodegenTypes.BubblingEventHandler<undefined>;
  points: Point[];
  innerRings?: (Point[])[];
  handled?: boolean;
  zI?: CodegenTypes.Float;
}

export default codegenNativeComponent<PolygonNativeProps>('PolygonView');
