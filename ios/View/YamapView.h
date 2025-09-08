#ifndef YamapView_h
#define YamapView_h

#import <React/RCTViewComponentView.h>

#import <YandexMapsMobile/YMKMapView.h>
#import <YandexMapsMobile/YMKUserLocation.h>
#import <YandexMapsMobile/YMKRequestPoint.h>
#import <YandexMapsMobile/YMKMapCameraListener.h>
#import <YandexMapsMobile/YMKMapLoadedListener.h>
#import <YandexMapsMobile/YMKTrafficListener.h>
#import <YandexMapsMobile/YMKClusterListener.h>
#import <YandexMapsMobile/YMKClusterTapListener.h>

NS_ASSUME_NONNULL_BEGIN

@interface YamapView: RCTViewComponentView

- (YMKMapView *)getMapView;

- (void)setClusterColor: (NSNumber *) color;
- (void)setClusteredMarkers:(NSArray<YMKPoint *> *) markers;
- (void)setClusterIcon:(NSString *)iconSource points:(NSArray<YMKPoint *> * _Nullable)points;
- (void)setClusterSize:(NSDictionary *)sizes;
- (void)setClusterTextColor:(NSNumber *)color;
- (void)setClusterTextSize:(double)size;
- (void)setClusterTextXOffset:(double)size;
- (void)setClusterTextYOffset:(double)size;

// REF

- (void)setCenter:(YMKCameraPosition *_Nonnull)position withDuration:(float)duration withAnimation:(int)animation;
- (void)setZoom:(float)zoom withDuration:(float)duration withAnimation:(int)animation;
- (void)fitAllMarkers:(float)duration animation:(int)animation;
- (void)fitMarkers:(NSArray<YMKPoint *> *_Nonnull)points duration:(float)duration animation:(int)animation;
- (void)setTrafficVisible:(BOOL)traffic;

@end

NS_ASSUME_NONNULL_END

#endif /* YamapView_h */
