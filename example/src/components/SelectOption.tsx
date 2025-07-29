import React, {Dispatch, FC} from 'react'
import {Platform, ScrollView, StyleSheet, View} from 'react-native'
import {OptionBubble} from './OptionBubble'
import {Screen, screens} from "../screens/screens";

interface Props {
  selectedScreen: Screen
  setSelectedScreen: Dispatch<Screen>
}

export const SelectOption: FC<Props> = ({selectedScreen, setSelectedScreen}) => {

  return (
    <View>
      <ScrollView
          horizontal
          showsHorizontalScrollIndicator={false}
          contentContainerStyle={styles.container}
      >
        {screens.map(screen =>
          <OptionBubble
            key={screen + (selectedScreen === screen ? 'selected' : '')}
            option={screen}
            selectOption={() => {
              setSelectedScreen(screen)
            }}
            selected={selectedScreen === screen}
          />
        )}
      </ScrollView>
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    alignItems: 'center',
    flexDirection: 'row',
    gap: 8,
    marginTop: Platform.OS === 'ios' ? 52 : 0,
    height: 50,
    paddingHorizontal: 16,
  },
})

