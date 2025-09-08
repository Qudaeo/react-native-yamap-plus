#ifndef PolylineView_h
#define PolylineView_h

#import <React/RCTViewComponentView.h>
#import <YandexMapsMobile/YMKPolyline.h>

NS_ASSUME_NONNULL_BEGIN

@interface PolylineView: RCTViewComponentView

- (YMKPolyline*)getPolyline;
- (YMKPolylineMapObject*)getMapObject;
- (void)setMapObject:(YMKPolylineMapObject*)mapObject;

@end

NS_ASSUME_NONNULL_END

#endif /* PolylineView_h */
