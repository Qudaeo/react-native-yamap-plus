#ifndef CircleView_h
#define CircleView_h

#import <React/RCTViewComponentView.h>
#import <YandexMapsMobile/YMKCircle.h>
#import <YandexMapsMobile/YMKPolygon.h>

NS_ASSUME_NONNULL_BEGIN

@interface CircleView: RCTViewComponentView

- (YMKCircle*)getCircle;
- (YMKPolygonMapObject*)getMapObject;
- (void)setMapObject:(YMKCircleMapObject*)mapObject;

@end

NS_ASSUME_NONNULL_END

#endif /* CircleView_h */
