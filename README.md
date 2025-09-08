## React Native Yandex Maps (Яндекс Карты)

#### Форк библиотеки [react-native-yamap](https://github.com/volga-volga/react-native-yamap), разработанной компанией [Волга-Волга](https://vvdev.ru/)

#### Библиотека для интеграции MapKit SDK в React Native

| Version | React Native New Arch support |
|---------|-------------------------------|
| 6(RC)   | New Arch                      |
| 5(RC)   | Legacy + New Arch             |
| 4       | Legacy Arch                   |

## Установка

```
yarn add react-native-yamap-plus
```

### Использование Lite версии Yandex MapKit SDK (без модулей Suggests, Search, Transport)

По умолчанию используется Full

Для изменения на Lite необходимо:

- android: в файле `android/build.gradle` добавить строку
```diff
buildscript {
    ext {
        ...
+       useYandexMapsLite = true
    }
```

- ios: в начало `Podfile` добавить строку
```diff
+ ENV['USE_YANDEX_MAPS_LITE'] = "1"
...
```

### Инициализировать карты

Для этого лучше всего зайти в корневой файл приложения, например `App.js`, и добавить инициализацию:

```js
import {YamapInstance} from 'react-native-yamap-plus';

YamapInstance.init(API_KEY);
```

### Изменение языка карт

```js
import {YamapInstance} from 'react-native-yamap-plus';

const currentLocale = await YamapInstance.getLocale();
YamapInstance.setLocale('en_US');  // 'ru_RU' или другие
YamapInstance.resetLocale();
```

-  **getLocale(): Promise\<string\>** - возвращает используемый язык карт;

-  **setLocale(locale: string): Promise\<void\>** - установить язык карт;

-  **resetLocale(): Promise\<void\>** - использовать для карт язык системы.

**ВАЖНО!**

1. Для **Android** изменение языка карт вступит в силу только после **перезапуска** приложения.
2. Для **iOS** методы изменения языка можно вызывать только до первого рендера карты. Также нельзя повторно вызывать метод, если язык уже изменялся (можно только после перезапуска приложения), иначе изменения приняты не будут, а в терминал будет выведено сообщение с предупреждением. В коде при этом не будет информации об ошибке.

### Использование компонента

```jsx
import React from 'react';
import {Yamap} from 'react-native-yamap-plus';

const Map = () => {
  return (
    <Yamap
      userLocationIcon={{ uri: 'https://www.clipartmax.com/png/middle/180-1801760_pin-png.png' }}
      initialRegion={{
        lat: 50,
        lon: 50,
        zoom: 10,
        azimuth: 80,
        tilt: 100
      }}
      style={{ flex: 1 }}
    />
  );
};
```

#### Доступные `props` для компонента **MapView**:

| Название | Тип | Стандартное значение | Описание |
|--|--|--|--|
| showUserPosition | boolean | false | Отслеживание геоданных и отображение позиции пользователя |
| followUser | boolean | false | слежение камеры за пользователем |
| userLocationIcon | ImageSource | false | Иконка для позиции пользователя. Доступны те же значения что и у компонента Image из React Native |
| userLocationIconScale | number | 1 | Масштабирование иконки пользователя |
| initialRegion | InitialRegion | | Изначальное местоположение карты при загрузке |
| interactiveDisabled | boolean | false | Интерактивная ли карта (перемещение по карте, отслеживание нажатий) |
| nightMode | boolean | false | Использование ночного режима |
| onMapLoaded | function | | Колбек на загрузку карты |
| onCameraPositionChange | function | | Колбек на изменение положения камеры |
| onCameraPositionChangeEnd | function | | Колбек при завершении изменения положения камеры |
| onMapPress | function | | Событие нажития на карту. Возвращает координаты точки на которую нажали |
| onMapLongPress | function | | Событие долгого нажития на карту. Возвращает координаты точки на которую нажали |
| userLocationAccuracyFillColor | string |  | Цвет фона зоны точности определения позиции пользователя |
| userLocationAccuracyStrokeColor | string |  | Цвет границы зоны точности определения позиции пользователя |
| userLocationAccuracyStrokeWidth | number | | Толщина зоны точности определения позиции пользователя |
| scrollGesturesDisabled | boolean | false | Включены ли жесты скролла |
| zoomGesturesDisabled | boolean | false | Включены ли жесты зума |
| tiltGesturesDisabled | boolean | false | Включены ли жесты наклона камеры двумя пальцами |
| rotateGesturesDisabled | boolean | false | Включены ли жесты поворота камеры |
| fastTapDisabled | boolean | false | Убрана ли задержка в 300мс при клике/тапе |
| clusterColor | string | 'red' | Цвет фона метки-кластера |
| logoPosition | YandexLogoPosition | {} | Позиция логотипа Яндекса на карте |
| logoPadding | YandexLogoPadding | {} | Отступ логотипа Яндекса на карте |
| mapType | string | 'vector' | Тип карты |
| mapStyle | string | {} | Стили карты согласно [документации](https://yandex.ru/dev/maps/mapkit/doc/dg/concepts/style.html) |

#### Доступные методы для компонента **MapView**:

-  `fitMarkers(points: Point[]): void` - подобрать положение камеры, чтобы вместить указанные маркеры (если возможно);
-  `fitAllMarkers(): void` - подобрать положение камеры, чтобы вместить все маркеры (если возможно);
-  `setCenter(center: { lon: number, lat: number }, zoom: number = 10, azimuth: number = 0, tilt: number = 0, duration: number = 0, animation: Animation = Animation.SMOOTH)` - устанавливает камеру в точку с заданным zoom, поворотом по азимуту и наклоном карты (`tilt`). Можно параметризовать анимацию: длительность и тип. Если длительность установить 0, то переход будет без анимации. Возможные типы анимаций `Animation.SMOOTH` и `Animation.LINEAR`;
-  `setZoom(zoom: number, duration: number, animation: Animation)` - изменить текущий zoom карты. Параметры `duration` и `animation` работают по аналогии с `setCenter`;
-  `getCameraPosition(callback: (position: CameraPosition) => void)` - запрашивает положение камеры и вызывает переданный колбек с текущим значением;
-  `getVisibleRegion(callback: (region: VisibleRegion) => void)` - запрашивает видимый регион и вызывает переданный колбек с текущим значением;
-  `setTrafficVisible(isVisible: boolean): void` - включить/отключить отображение слоя с пробками на картах;
-  `getScreenPoints(point: Point[], callback: (result: {screenPoints: ScreenPoint[]}) => void)` - получить кооординаты на экране (x и y) по координатам маркеров;
-  `getWorldPoints(screenPoint: ScreenPoint[], callback: (result: {worldPoints: Point[]}) => void)` - получить координаты точек (lat и lon) по координатам на экране.

**ВАЖНО**

- Компонент карт стилизуется, как и `View` из React Native. Если карта не отображается, после инициализации с валидным ключем API, вероятно необходимо прописать стиль, который опишет размеры компонента (`height + width` или `flex`);
- При использовании изображений из JS (через `require('./img.png')`) в дебаге и релизе на Android могут быть разные размеры маркера. Рекомендуется проверять рендер в релизной сборке.

## Отображение примитивов

### Marker

```jsx
import {Yamap, Marker} from 'react-native-yamap-plus';

<Yamap>
  <Marker point={{ lat: 50, lon: 50 }} />
</Yamap>
```

#### Доступные `props` для примитива **Marker**:

| Название | Тип | Описание |
|--|--|--|
| point | Point | Координаты точки для отображения маркера |
| scale | number | Масштабирование иконки маркера. Не работает если использовать children у маркера |
| source | ImageSource | Данные для изображения маркера |
| children | ReactElement | Рендер маркера как компонента |
| onPress | function | Действие при нажатии/клике |
| anchor | {  x:  number,  y:  number  } | Якорь иконки маркера. Координаты принимают значения от 0 до 1 |
| zI | number | zIndex для объекта на карте |
| visible | boolean | Отображение маркера на карте |
| handled | boolean | Включение(**false**)/отключение(**true**) всплытия события нажатия для родителя `default:false` |

#### Доступные методы для примитива **Marker**:

-  `animatedMoveTo(point: Point, duration: number)` - плавное изменение позиции маркера;
-  `animatedRotateTo(angle: number, duration: number)` - плавное вращение маркера.

### Circle

```jsx
import {Yamap, Circle} from 'react-native-yamap-plus';

<Yamap>
  <Circle center={{ lat: 50, lon: 50 }} radius={300} />
</Yamap>
```

#### Доступные `props` для примитива **Circle**:

| Название | Тип | Описание |
|--|--|--|
| center | Point | Координаты центра круга |
| radius | number | Радиус круга в метрах |
| fillColor | string | Цвет заливки |
| strokeColor | string | Цвет границы |
| strokeWidth | number | Толщина границы |
| onPress | function | Действие при нажатии/клике |
| zI | number | zIndex для объекта на карте |
| handled | boolean | Включение(**false**)/отключение(**true**) всплытия события нажатия для родителя `default:true` |

### Polyline

```jsx
import {Yamap, Polyline} from 'react-native-yamap-plus';

<Yamap>
  <Polyline
    points={[
      { lat: 50, lon: 50 },
      { lat: 50, lon: 20 },
      { lat: 20, lon: 20 },
    ]}
  />
</Yamap>
```

#### Доступные `props` для примитива **Polyline**:

| Название | Тип | Описание |
|--|--|--|
| points | Point[] | Массив точек линии |
| strokeColor | string | Цвет линии |
| strokeWidth | number | Толщина линии |
| outlineColor | string | Цвет обводки |
| outlineWidth | number | Толщина обводки |
| dashLength | number | Длина штриха |
| dashOffset | number | Отступ первого штриха от начала полилинии |
| gapLength | number | Длина разрыва между штрихами |
| onPress | function | Действие при нажатии/клике |
| zI | number | zIndex для объекта на карте |
| handled | boolean | Включение(**false**)/отключение(**true**) всплытия события нажатия для родителя `default:false` |

### Polygon

```jsx
import {Yamap, Polygon} from 'react-native-yamap-plus';

<Yamap>
  <Polygon
    points={[
      { lat: 50, lon: 50 },
      { lat: 50, lon: 20 },
      { lat: 20, lon: 20 },
    ]}
  />
</Yamap>
```

#### Доступные `props` для примитива **Polygon**:

| Название | Тип | Описание |
|--|--|--|
| points | Point[] | Массив точек линии |
| fillColor | string | Цвет заливки |
| strokeColor | string | Цвет границы |
| strokeWidth | number | Толщина границы |
| innerRings | (Point[])[] | Массив полилиний, которые образуют отверстия в полигоне |
| onPress | function | Действие при нажатии/клике |
| zI | number | zIndex для объекта на карте |
| handled | boolean | Включение(**false**)/отключение(**true**) всплытия события нажатия для родителя `default:false` |

## Запрос маршрутов (модуль Transport) [Full]

```typescript
import {Transport} from 'react-native-yamap-plus';

const routes = await Transport.findRoutes(points, vehicles) // универсальный поиск маршрутов
const masstransitRoutes = await findMasstransitRoutes(points) // маршрутов на общественном транспорте
const pedestrianRoutes = await findPedestrianRoutes(points) // пешеходные маршруты
const drivingRoutes = await findDrivingRoutes(points) // маршруты на автомобиле
```

Тип роутера зависит от переданного в функцию массива `vehicles`:

- Если передан пустой массив (`findRoutes(points, [])`), то будет использован `PedestrianRouter`;
- Если передан массив с одним элементом `'car'` (`findRoutes(points, ['car'])`), то будет использован `DrivingRouter`;
- Во всех остальных случаях используется `MasstransitRouter`.

## Поиск по гео с подсказсками (GeoSuggestions) [Full]

Для поиска с геоподсказками нужно воспользоваться модулем Suggest:

```typescript

import {Suggest} from 'react-native-yamap-plus';

const find = async (query: string, options?: SuggestOptions) => {
  const suggestions = await Suggest.suggest(query, options);

  // suggestion = [{
  //   subtitle: "Москва, Россия"
  //   title: "улица Льва Толстого, 16"
  //   uri: "ymapsbm1://geo?ll=37.587093%2C55.733974&spn=0.001000%2C0.001000&text=%D0%A0%D0%BE%D1%81%D1%81%D0%B8%D1%8F%2C%20%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0%2C%20%D1%83%D0%BB%D0%B8%D1%86%D0%B0%20%D0%9B%D1%8C%D0%B2%D0%B0%20%D0%A2%D0%BE%D0%BB%D1%81%D1%82%D0%BE%D0%B3%D0%BE%2C%2016"
  // }, ...]

  const suggestionsWithCoards = await Suggest.suggestWithCoords(query, options);

  // suggestionsWithCoards = [{
  //   subtitle: "Москва, Россия"
  //   title: "улица Льва Толстого, 16"
  //   lat: 55.733974
  //   lon: 37.587093
  //   uri: "ymapsbm1://geo?ll=37.587093%2C55.733974&spn=0.001000%2C0.001000&text=%D0%A0%D0%BE%D1%81%D1%81%D0%B8%D1%8F%2C%20%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0%2C%20%D1%83%D0%BB%D0%B8%D1%86%D0%B0%20%D0%9B%D1%8C%D0%B2%D0%B0%20%D0%A2%D0%BE%D0%BB%D1%81%D1%82%D0%BE%D0%B3%D0%BE%2C%2016"
  // }, ...]

  // After searh session is finished
  Suggest.reset();
}
```
## Поиск по гео (GeoSearch) [Full]

Для поиска нужно воспользоваться модулем Suggest:

```typescript

import {Search} from 'react-native-yamap-plus';

const find = async (query: string, options?: SuggestOptions) => {
  // можно использовать Point, BoundingBox, Polyline и Polygon (4 точки, без innerRings)
  const search = await Search.searchText(
    'Москва',
    { type: GeoFigureType.POINT, value: {lat: 54, lon: 53}},
    { disableSpellingCorrection: true, geometry: true },
  );

  // второй параметр это зум, определяющий на сколько малые объекты искать
  const searchByPoint = await Search.searchPoint({lat: 54, lon: 53}, 10, {
    disableSpellingCorrection: true,
    geometry: true,
  });

  const resolveURI = await Search.resolveURI("ymapsbm1://geo?data=IgoNAQBYQhUBAFhC", {
    disableSpellingCorrection: true,
    geometry: true,
  });

  const searchByURI = await Search.searchByURI("ymapsbm1://geo?data=IgoNAQBYQhUBAFhC", {
    disableSpellingCorrection: true,
    geometry: true,
  });
  
//   {"Components": [{"kind": "4", "name": "Малиновский сельсовет"}, {"kind": "4", "name": "Белебеевский район"}, {"kind": "3", "name": "Республика Башкортостан"}, {"kind": "19", "name": "Понтийско-Каспийская степь"}, {"kind": "19", "name": "Понтийско-Каспийская степь"}, {"kind": "19", "name": "Понтийско-Каспийская степь"}, {"kind": "3", "name": "Приволжский федеральный округ"}, {"kind": "1", "name": "Россия"}], "country_code": "RU", "formatted": "Россия, Республика Башкортостан, Белебеевский район, Малиновский сельсовет", "uri": "ymapsbm1://geo?data=IgoNAQBYQhUBAFhC"}
  
}
```

Также теперь можно воспользоваться геокодированием из поиска

```typescript

import {Search} from 'react-native-yamap-plus';

const address = Search.geocodePoint({lat: 54, lon: 53});

// {"Components": [{"kind": "4", "name": "Малиновский сельсовет"}, {"kind": "4", "name": "Белебеевский район"}, {"kind": "3", "name": "Республика Башкортостан"}, {"kind": "19", "name": "Понтийско-Каспийская степь"}, {"kind": "19", "name": "Понтийско-Каспийская степь"}, {"kind": "19", "name": "Понтийско-Каспийская степь"}, {"kind": "3", "name": "Приволжский федеральный округ"}, {"kind": "1", "name": "Россия"}], "country_code": "RU", "formatted": "Россия, Республика Башкортостан, Белебеевский район, Малиновский сельсовет", "uri": "ymapsbm1://geo?data=IgoNAQBYQhUBAFhC"}

const point = Search.geocodeAddress(address.formatted);

// возвращает координаты по адресу {"lat": 53.999187242158015, "lon": 54.089440735780194}

```


### Использование компонента ClusteredYamap

```jsx
import React from 'react';
import {ClusteredYamap} from 'react-native-yamap-plus';

const Map = () => {
  return (
    <ClusteredYamap
      clusterColor="red"
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
      renderMarker={(info, index) => (
        <Marker
          key={index}
          point={info.point}
        />
      )}
      style={{flex: 1}}
    />
  );
};
```

# Использование с Expo
Для подключения нативного модуля в приложение с expo используйте expo prebuild.
Он выполнит eject и сгенерирует привычные папки android и ios с нативным кодом. Это позволит использовать любую библиотеку так же, как и приложение с react native cli.

# Проблемы

1. В никоторых случаях в симуляторе ios может крашиться приложение с ошибкой
```failed assertion `The following Metal object is being destroyed while still required to be alive by the command buffer```

Решение: In Xcode, in the "Product" Menu, go to "Scheme", then "Edit Scheme...". In the left panel select "Run (Debug)" and in Diagnostic tab uncheck "API Validation" under Metal settings.
