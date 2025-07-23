#ifndef TransportUtils_h
#define TransportUtils_h

#import <YandexMapsMobile/YMKTransport.h>

#ifdef RCT_NEW_ARCH_ENABLED

#import <react/renderer/components/RNYamapPlusSpec/ComponentDescriptors.h>
#import <react/renderer/components/RNYamapPlusSpec/EventEmitters.h>
#import <react/renderer/components/RNYamapPlusSpec/Props.h>
#import <react/renderer/components/RNYamapPlusSpec/RCTComponentViewHelpers.h>

#import "RCTFabricComponentsPlugins.h"

using namespace facebook::react;

#endif

@interface TransportUtils : NSObject;

#ifdef RCT_NEW_ARCH_ENABLED

- (void)findRoutes:(NSArray<YMKRequestPoint *> *)_points vehicles:(NSArray<NSString *> *)vehicles withId:(NSString *)_id competition:(void (^)(YamapViewEventEmitter::OnRouteFound response))completion;

#else

- (void)findRoutes:(NSArray<YMKRequestPoint *> *)_points vehicles:(NSArray<NSString *> *)vehicles withId:(NSString *)_id competition:(void (^)(NSDictionary *response))completion;

#endif

@end

#endif
