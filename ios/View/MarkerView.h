#ifndef MarkerView_h
#define MarkerView_h

#import <React/RCTViewComponentView.h>

#import <YandexMapsMobile/YMKPlacemark.h>

NS_ASSUME_NONNULL_BEGIN

@interface MarkerView: RCTViewComponentView

- (YMKPoint*)getPoint;
- (YMKPlacemarkMapObject*)getMapObject;
- (void)setMapObject:(YMKPlacemarkMapObject*)mapObject;
- (void)setClusterMapObject:(YMKPlacemarkMapObject*)mapObject;

@end

NS_ASSUME_NONNULL_END

#endif /* MarkerView_h */
