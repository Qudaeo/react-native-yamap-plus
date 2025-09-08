#ifndef PolygonView_h
#define PolygonView_h

#import <React/RCTViewComponentView.h>
#import <YandexMapsMobile/YMKPolygon.h>

NS_ASSUME_NONNULL_BEGIN

@interface PolygonView: RCTViewComponentView

- (YMKPolygon*)getPolygon;
- (YMKPolygonMapObject*)getMapObject;
- (void)setMapObject:(YMKPolygonMapObject*)mapObject;

@end

NS_ASSUME_NONNULL_END

#endif /* PolygonView_h */
