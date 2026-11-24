#ifndef MarkerView_h
#define MarkerView_h

#import <React/RCTViewComponentView.h>

#import <YandexMapsMobile/YMKPlacemark.h>

NS_ASSUME_NONNULL_BEGIN

@interface MarkerView: RCTViewComponentView

// nil until the marker's props arrive: Fabric mounts the view before setting props
- (nullable YMKPoint*)getPoint;
- (YMKPlacemarkMapObject*)getMapObject;
- (void)setMapObject:(YMKPlacemarkMapObject*)mapObject;
- (void)setClusterMapObject:(nullable YMKPlacemarkMapObject*)mapObject;
- (BOOL)excludeFromCluster;

@end

NS_ASSUME_NONNULL_END

#endif /* MarkerView_h */
