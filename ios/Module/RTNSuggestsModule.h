#ifdef USE_YANDEX_MAPS_FULL

#import <YandexMapsMobile/YMKSearchManager.h>
#import <YandexMapsMobile/YMKSuggestOptions.h>

#endif

#import <RNYamapPlusSpec/RNYamapPlusSpec.h>

@interface RTNSuggestsModule : NativeSuggestsModuleSpecBase <NativeSuggestsModuleSpec>

#ifdef USE_YANDEX_MAPS_FULL

@property YMKSearchManager *searchManager;
@property YMKSearchSuggestSession *suggestClient;
@property YMKBoundingBox *defaultBoundingBox;
@property YMKSuggestOptions *defaultSuggestOptions;

#endif

@end
