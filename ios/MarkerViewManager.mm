#ifndef RCT_NEW_ARCH_ENABLED

#import <React/RCTUIManager.h>

#import "View/MarkerView.h"
#import "Util/RCTConvert+Yamap.mm"

@interface MarkerViewManager : RCTViewManager
@end

@implementation MarkerViewManager

RCT_EXPORT_MODULE(MarkerView)

- (NSArray<NSString*>*)supportedEvents {
    return @[@"onPress"];
}

- (UIView *)view {
    return [[MarkerView alloc] init];
}

// PROPS
RCT_EXPORT_VIEW_PROPERTY(onPress, RCTBubblingEventBlock)

RCT_EXPORT_VIEW_PROPERTY(point, YMKPoint)
RCT_EXPORT_VIEW_PROPERTY(scale, NSNumber)
RCT_EXPORT_VIEW_PROPERTY(rotated, NSNumber)
RCT_EXPORT_VIEW_PROPERTY(visible, NSNumber)
RCT_EXPORT_VIEW_PROPERTY(handled, BOOL)
RCT_EXPORT_VIEW_PROPERTY(anchor, NSValue)
RCT_EXPORT_VIEW_PROPERTY(zI, NSNumber)
RCT_EXPORT_VIEW_PROPERTY(source, NSString)

// REF
RCT_EXPORT_METHOD(animatedMoveTo:(nonnull NSNumber*)reactTag argsArr:(NSArray*)argsArr) {
    @try  {
        [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber*, UIView*> *viewRegistry) {
            MarkerView* view = (MarkerView*)viewRegistry[reactTag];

            NSDictionary* args = argsArr.firstObject;
            YMKPoint* point = [RCTConvert YMKPoint:args[@"coords"]];
            [view animatedMoveTo:point withDuration:[args[@"duration"] floatValue]];
        }];
    } @catch (NSException *exception) {
        NSLog(@"Marker animatedMoveTo error: %@ ",exception.reason);
    }
}

RCT_EXPORT_METHOD(animatedRotateTo:(nonnull NSNumber*)reactTag argsArr:(NSArray*)argsArr) {
    @try  {
        [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber*, UIView*> *viewRegistry) {
            MarkerView* view = (MarkerView*)viewRegistry[reactTag];

            NSDictionary* args = argsArr.firstObject;
            [view animatedRotateTo:[args[@"angle"] floatValue] withDuration:[args[@"duration"] floatValue]];
        }];
    } @catch (NSException *exception) {
        NSLog(@"Marker animatedRotateTo error: %@ ",exception.reason);
    }
}

@end

#endif
