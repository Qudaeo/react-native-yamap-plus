import React, {useEffect, useRef, useState} from 'react';
import {StyleSheet} from 'react-native';
import {ClusteredYamap, Marker, YamapRef} from '../../../';

export const ClusteredMapScreen = () => {
  const clusteredMapRef = useRef<YamapRef | null>(null);
  const [mapLoaded, setMapLoaded] = useState(false);

    useEffect(() => {
      if (mapLoaded) {
        clusteredMapRef.current?.getCameraPosition(e => {
          console.log('clustered getCameraPosition', e);
        });
        clusteredMapRef.current?.getVisibleRegion(e => {
          console.log('clustered getVisibleRegion', e);
        });
        clusteredMapRef.current?.getWorldPoints([{x: 100, y: 100}], e => {
          console.log('clustered getWorldPoints', e);
        });
        clusteredMapRef.current?.getScreenPoints([{lat: 55.75124399961543, lon: 37.618422999999986}], e => {
          console.log('clustered getScreenPoints', e);
        });
        // clusteredMapRef.current?.findRoutes([{lat: 55.75, lon: 37.61}, {lat: 55.76, lon: 37.62}], ['walk'], e => {
        //   console.log('clustered findRoutes', e);
        // });
        // setTimeout(() => clusteredMapRef.current?.setTrafficVisible(true), 3000);
        // setTimeout(() => clusteredMapRef.current?.setCenter({lat: 56, lon: 38}), 3000);
        // setTimeout(() => clusteredMapRef.current?.fitAllMarkers(), 3000);
        // setTimeout(() => clusteredMapRef.current?.fitMarkers([{lat: 56, lon: 38}]), 3000);
        // setTimeout(() => clusteredMapRef.current?.setZoom(10, 1), 3000);
      }
    }, [mapLoaded]);

  return (
    <ClusteredYamap
      ref={clusteredMapRef}
      clusterColor="red"
      initialRegion={{lat: 56.754215, lon: 38.421242, zoom: 6}}
      onMapLoaded={(e) => {
        console.log('clustered onMapLoaded', e.nativeEvent);
        setMapLoaded(true);
      }}
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
      clusteredMarkers={[
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
      ]}
      renderMarker={(info) => (
        <Marker
          key={`${info.point.lat}_${info.point.lon}`}
          point={info.point}
          scale={0.3}
          source={require('../assets/images/marker.png')}
          visible={mapLoaded}
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
