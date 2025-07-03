#ifndef YamapView_h
#define YamapView_h

#ifdef RCT_NEW_ARCH_ENABLED

#import <React/RCTViewComponentView.h>

#else

#import <React/RCTComponent.h>

#endif

#import <YandexMapsMobile/YMKMapView.h>
#import <YandexMapsMobile/YMKUserLocation.h>
#import <YandexMapsMobile/YMKRequestPoint.h>
#import <YandexMapsMobile/YMKMapCameraListener.h>
#import <YandexMapsMobile/YMKMapLoadedListener.h>
#import <YandexMapsMobile/YMKTrafficListener.h>
#import <YandexMapsMobile/YMKClusterListener.h>
#import <YandexMapsMobile/YMKClusterTapListener.h>

NS_ASSUME_NONNULL_BEGIN

#ifdef RCT_NEW_ARCH_ENABLED

@interface YamapView: RCTViewComponentView

#else

@interface YamapView: UIView<YMKUserLocationObjectListener, YMKMapCameraListener, YMKMapLoadedListener, YMKTrafficDelegate, YMKClusterListener, YMKClusterTapListener>

@property (nonatomic, copy) RCTBubblingEventBlock onRouteFound;
@property (nonatomic, copy) RCTBubblingEventBlock onCameraPositionReceived;
@property (nonatomic, copy) RCTBubblingEventBlock onVisibleRegionReceived;
@property (nonatomic, copy) RCTBubblingEventBlock onCameraPositionChange;
@property (nonatomic, copy) RCTBubblingEventBlock onCameraPositionChangeEnd;
@property (nonatomic, copy) RCTBubblingEventBlock onMapPress;
@property (nonatomic, copy) RCTBubblingEventBlock onMapLongPress;
@property (nonatomic, copy) RCTBubblingEventBlock onMapLoaded;
@property (nonatomic, copy) RCTBubblingEventBlock onWorldToScreenPointsReceived;
@property (nonatomic, copy) RCTBubblingEventBlock onScreenToWorldPointsReceived;

#endif

- (YMKMapView *)getMapView;

- (void)setClusterColor: (NSNumber *) color;
- (void)setClusteredMarkers:(NSArray<YMKPoint *> *) markers;

// REF
- (void)emitCameraPositionToJS:(NSString *_Nonnull)_id;
- (void)emitVisibleRegionToJS:(NSString *_Nonnull)_id;
- (void)setCenter:(YMKCameraPosition *_Nonnull)position withDuration:(float)duration withAnimation:(int)animation;
- (void)setZoom:(float)zoom withDuration:(float)duration withAnimation:(int)animation;
- (void)fitAllMarkers;
- (void)fitMarkers:(NSArray<YMKPoint *> *_Nonnull)points;
- (void)findRoutes:(NSArray<YMKRequestPoint *> *_Nonnull)points vehicles:(NSArray<NSString *> *_Nonnull)vehicles withId:(NSString *_Nonnull)_id;
- (void)setTrafficVisible:(BOOL)traffic;
- (void)emitWorldToScreenPoint:(NSArray<YMKPoint *> *_Nonnull)points withId:(NSString*_Nonnull)_id;
- (void)emitScreenToWorldPoint:(NSArray<YMKScreenPoint *> *_Nonnull)points withId:(NSString*_Nonnull)_id;

@end

NS_ASSUME_NONNULL_END

#endif /* YamapView_h */
