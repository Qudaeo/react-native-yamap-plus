#import <YandexMapsMobile/YMKSearchManager.h>

#ifdef RCT_NEW_ARCH_ENABLED

#import <RNYamapPlusSpec/RNYamapPlusSpec.h>
@interface RTNSearchModule : NativeSearchModuleSpecBase <NativeSearchModuleSpec>

#else

#import <React/RCTBridgeModule.h>
@interface RTNSearchModule : NSObject <RCTBridgeModule>

#endif

@property YMKSearchManager *searchManager;
@property YMKBoundingBox *defaultBoundingBox;
@property YMKSearchSession *searchSession;

@end
