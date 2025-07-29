import React, {useEffect, useState} from 'react';
import {StyleSheet, Text, TextInput, View} from 'react-native';
import {Address, Point, Search} from '../../../';
import {useDebounceFunc} from '../helper/debounce';

export const SearchScreen = () => {
  const [text, setText] = useState('Moscow');
  const [point, setPoint] = useState<Point | {}>({});
  const [address, setAddress] = useState<Address | {}>();

  const search = useDebounceFunc(async (searchText: string) => {
    try {
      if (searchText.trim()) {
        const point1 = await Search.geocodeAddress(searchText);
        setPoint(point1);

        if (point1.lat) {
          const address1 = await Search.geocodePoint(point1);
          setAddress(address1);

          // await Search.searchPoint(point1, 10, {searchTypes: [SearchType.GEO]})
          //   .then(res => console.log('Search.searchPoint', res));

          // await Search.searchText(searchText, {type: GeoFigureType.POINT, value: {lon: 50, lat: 50}}, {searchTypes: [SearchType.GEO]})
          //   .then(res => console.log('Search.searchText', res));
        }
      }
    } catch (e) {
      console.error('search', e);
    }
  });

  useEffect( () => {
    setPoint({});
    setAddress({});
    search(text);
    /* eslint-disable-next-line react-hooks/exhaustive-deps */
  }, [text]);

  return (
    <View style={styles.container}>
      <TextInput
          value={text}
          onChangeText={setText}
          placeholder={'Address'}
          style={styles.textInput}
      />
      <Text style={styles.text}>{`Search.geocodeAddress: ${JSON.stringify(point)}`}</Text>
      <Text style={styles.text}>{`Search.geocodePoint: ${JSON.stringify(address)}`}</Text>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    paddingHorizontal: 16,
  },
  textInput: {
    height: 50,
    borderWidth: 1,
    borderColor: '#aaa',
    borderRadius: 8,
    padding: 10,
    marginVertical: 16,
  },
  text: {
    color: '#000',
    alignSelf: 'flex-start',
    marginTop: 16,
  },
});
