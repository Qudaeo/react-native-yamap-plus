import React, {useRef} from 'react';
import {StyleSheet, View, type CodegenTypes} from 'react-native';
import {ClusteredYamap, Marker, ClusteredYamapRef} from '../../../';
import {MapLoaded} from '../../../src';

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

const PANEL_HEIGHT = 240;

export const ClusteredMapScreen = () => {
  const clusteredMapRef = useRef<ClusteredYamapRef | null>(null);

  const onMapLoaded: CodegenTypes.DirectEventHandler<MapLoaded> = (event) => {
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
    <View style={styles.container}>
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
        focusInsets={{bottom: PANEL_HEIGHT}}
        style={styles.container}
      />
      <View style={styles.panel} />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  panel: {
    position: 'absolute',
    left: 0,
    right: 0,
    bottom: 0,
    height: PANEL_HEIGHT,
    backgroundColor: 'rgba(255, 255, 255, 0.85)',
    pointerEvents: 'none',
  },
});
