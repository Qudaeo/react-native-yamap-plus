import {codegenNativeComponent, type CodegenTypes, type ViewProps} from 'react-native';

interface Point {
  lat: CodegenTypes.Double;
  lon: CodegenTypes.Double;
}

export interface CircleNativeProps extends ViewProps {
  fillColor?: CodegenTypes.Int32;
  strokeColor?: CodegenTypes.Int32;
  strokeWidth?: CodegenTypes.Float;
  onPress?: CodegenTypes.BubblingEventHandler<undefined>;
  center: Point;
  radius: CodegenTypes.Float;
  handled?: boolean;
  zI?: CodegenTypes.Float;
}

export default codegenNativeComponent<CircleNativeProps>('CircleView');
