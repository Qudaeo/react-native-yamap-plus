import {codegenNativeComponent, type CodegenTypes, type ViewProps} from 'react-native';

interface Point {
  lat: CodegenTypes.Double;
  lon: CodegenTypes.Double;
}

export interface PolylineNativeProps extends ViewProps {
  strokeColor?: CodegenTypes.Int32;
  outlineColor?: CodegenTypes.Int32;
  strokeWidth?: CodegenTypes.Float;
  outlineWidth?: CodegenTypes.Float;
  dashLength?: CodegenTypes.Float;
  dashOffset?: CodegenTypes.Float;
  gapLength?: CodegenTypes.Float;
  onPress?: CodegenTypes.BubblingEventHandler<undefined>;
  points: Point[];
  handled?: boolean;
  zI?: CodegenTypes.Float;
}

export default codegenNativeComponent<PolylineNativeProps>('PolylineView');
