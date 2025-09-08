#ifdef USE_YANDEX_MAPS_FULL

#import <YandexMapsMobile/YMKSearchManager.h>

#endif

#import <RNYamapPlusSpec/RNYamapPlusSpec.h>

@interface RTNSearchModule : NativeSearchModuleSpecBase <NativeSearchModuleSpec>

#ifdef USE_YANDEX_MAPS_FULL

@property YMKSearchManager *searchManager;
@property YMKBoundingBox *defaultBoundingBox;
@property YMKSearchSession *searchSession;

#endif

@end
