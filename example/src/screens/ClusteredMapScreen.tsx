import React, {useRef} from 'react';
import {StyleSheet} from 'react-native';
import {DirectEventHandler} from "react-native/Libraries/Types/CodegenTypes";
import {ClusteredYamap, Marker, YamapRef} from '../../../';
import {MapLoaded} from "../../../src/spec/YamapNativeComponent.ts";

const markers = [
  {
    point: {
      lat: 56.754215,
      lon: 38.622504,
    },
    data: {},
  },
  {
    point: {
      lat: 56.754215,
      lon: 38.222504,
    },
    data: {},
  },
];

export const ClusteredMapScreen = () => {
  const clusteredMapRef = useRef<YamapRef | null>(null);

  const onMapLoaded: DirectEventHandler<MapLoaded> = (event) => {
    console.log('clustered onMapLoaded', event.nativeEvent);
    // clusteredMapRef.current?.getCameraPosition(e => {
    //   console.log('clustered getCameraPosition', e);
    // });
    // clusteredMapRef.current?.getVisibleRegion(e => {
    //   console.log('clustered getVisibleRegion', e);
    // });
    // clusteredMapRef.current?.getWorldPoints([{x: 100, y: 100}], e => {
    //   console.log('clustered getWorldPoints', e);
    // });
    // clusteredMapRef.current?.getScreenPoints([{lat: 55.75124399961543, lon: 37.618422999999986}], e => {
    //   console.log('clustered getScreenPoints', e);
    // });
    // setTimeout(() => clusteredMapRef.current?.setTrafficVisible(true), 3000);
    // setTimeout(() => clusteredMapRef.current?.setCenter({lat: 56, lon: 38}), 3000);
    // setTimeout(() => clusteredMapRef.current?.fitAllMarkers(), 3000);
    // setTimeout(() => clusteredMapRef.current?.fitMarkers([{lat: 56, lon: 38}]), 3000);
    // setTimeout(() => clusteredMapRef.current?.setZoom(10, 1), 3000);
  }

  return (
    <ClusteredYamap
      ref={clusteredMapRef}
      clusterColor="green"
      clusterIcon={require('../assets/images/octagon.png')}
      clusterSize={{width: 35, height: 35}}
      clusterTextColor={'red'}
      initialRegion={{lat: 56.754215, lon: 38.421242, zoom: 6}}
      onMapLoaded={onMapLoaded}
      onCameraPositionChange={e => {
        console.log('clustered onCameraPositionChange', e.nativeEvent);
      }}
      onCameraPositionChangeEnd={e => {
        console.log('clustered onCameraPositionChangeEnd', e.nativeEvent);
      }}
      onMapPress={e => {
        console.log('clustered map onPress', e.nativeEvent);
      }}
      onMapLongPress={(e) => {
        console.log('clustered map onLongPress', e.nativeEvent);
      }}
      clusteredMarkers={markers}
      renderMarker={(info) => (
        <Marker
          key={`${info.point.lat}_${info.point.lon}`}
          point={info.point}
          scale={0.3}
          source={require('../assets/images/marker.png')}
          anchor={{x: 0.5, y: 1}}
        />
      )}
      style={styles.container}
    />
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
});
