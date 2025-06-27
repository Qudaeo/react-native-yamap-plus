import React, {useEffect, useRef, useState} from 'react';
import {Platform, StyleSheet} from 'react-native';
import {Yamap, Circle, Marker, MarkerRef, Polyline, YamapRef} from '../../../';
import {Polygon} from '../../../src';

export const MapScreen = () => {
  const [mapLoaded, setMapLoaded] = useState(false);
  const mapRef = useRef<YamapRef | null>(null);
  const markerRef = useRef<MarkerRef | null>(null);
  const angleRef = useRef(0);

  useEffect(() => {
    if (mapLoaded) {
      // mapRef.current?.getCameraPosition(e => {
      //   console.log('getCameraPosition', e);
      // });
      // mapRef.current?.getVisibleRegion(e => {
      //   console.log('getVisibleRegion', e);
      // });
      // mapRef.current?.getWorldPoints([{x: 100, y: 100}], e => {
      //   console.log('getWorldPoints', e);
      // });
      // mapRef.current?.getScreenPoints([{lat: 55.75124399961543, lon: 37.618422999999986}], e => {
      //   console.log('getScreenPoints', e);
      // });
      // mapRef.current?.findRoutes([{lat: 55.75, lon: 37.61}, {lat: 55.76, lon: 37.62}], ['walk'], e => {
      //   console.log('findRoutes', e);
      // });
      // mapRef.current?.findDrivingRoutes([{lat: 55.75, lon: 37.61}, {lat: 55.76, lon: 37.62}], e => {
      //   console.log(Platform.OS, 'findDrivingRoutes', e);
      // });
      // setTimeout(() => mapRef.current?.setTrafficVisible(true), 3000);
      //setTimeout(() => mapRef.current?.setCenter({lat: 56, lon: 38}), 3000);
      //setTimeout(() => mapRef.current?.fitAllMarkers(), 3000);
      //setTimeout(() => mapRef.current?.fitMarkers([{lat: 56, lon: 38}]), 3000);
      //setTimeout(() => mapRef.current?.setZoom(10, 1), 3000);
    }
  }, [mapLoaded]);

  return (
    <Yamap
      ref={mapRef}
      initialRegion={{lat: 55.751244, lon: 37.618423, zoom: 12}}
      style={styles.container}
      logoPosition={{horizontal: 'right', vertical: 'top'}}
      onMapLoaded={(e) => {
        console.log('onMapLoaded', e.nativeEvent);
        setMapLoaded(true);
      }}
      onCameraPositionChange={e => {
          console.log('onCameraPositionChange', e.nativeEvent);
      }}
      onCameraPositionChangeEnd={e => {
          console.log('onCameraPositionChangeEnd', e.nativeEvent);
      }}
      onMapPress={e => {
        console.log('map onPress', e.nativeEvent);
        markerRef.current?.animatedMoveTo(e.nativeEvent, 500);
      }}
      onMapLongPress={(e) => {
        console.log('map onLongPress', e.nativeEvent);
      }}
    >
      <Marker
        ref={markerRef}
        point={{lat: 55.751244, lon: 37.618423}}
        source={require('../assets/images/marker.png')}
        scale={0.25}
        visible={Platform.OS === 'android' ? mapLoaded : true}
        rotated={true}
        onPress={() => {
           console.log('marker onPress');
        }}
        anchor={{x: 0.5, y: 1}}
      />
      <Circle
        center={{lat: 55.74, lon: 37.64}}
        radius={500}
        strokeWidth={5}
        strokeColor={'red'}
        fillColor={'blue'}
        onPress={() => {
          console.log('circle onPress');
          angleRef.current = angleRef.current + 180;
          markerRef.current?.animatedRotateTo(angleRef.current, 300);
        }}
        zI={100}
      />
      <Polygon
        points={[
          {lat: 55.74, lon: 37.57},
          {lat: 55.7, lon: 37.6},
          {lat: 55.72, lon: 37.64},
          {lat: 55.77, lon: 37.64},
        ]}
        fillColor={'green'}
        strokeColor={'blue'}
        strokeWidth={3}
        handled={false}
        onPress={() => {
          console.log('polygon press');
        }}
        zI={5}
        innerRings={[
          [
            {lat: 55.735, lon: 37.58},
            {lat: 55.71, lon: 37.61},
            {lat: 55.72, lon: 37.63},
          ],
      ]}
      />
      <Polygon
        points={[
          {lat: 55.77, lon: 37.57},
          {lat: 55.7, lon: 37.62},
          {lat: 55.78, lon: 37.60},
        ]}
        fillColor={'red'}
        strokeWidth={0}
        handled={true}
        onPress={() => {
          console.log('polygon press');
        }}
        zI={7}
      />
      <Polyline
        points={[
          {lat: 55.78, lon: 37.6},
          {lat: 55.76, lon: 37.57},
          {lat: 55.78, lon: 37.64},
          {lat: 55.79, lon: 37.6},
        ]}
        strokeWidth={4}
        strokeColor={'rgb(10,10,10)'}
        outlineColor={'orange'}
        outlineWidth={2}
        gapLength={5}
        dashLength={20}
        handled={true}
        onPress={() => {
          console.log('polyline press');
        }}
        zI={11}
      />
    </Yamap>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
});
