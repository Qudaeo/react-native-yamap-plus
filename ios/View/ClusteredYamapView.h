#ifndef ClusteredYamapView_h
#define ClusteredYamapView_h

#import <YamapView.h>

#import <YandexMapsMobile/YMKClusterListener.h>
#import <YandexMapsMobile/YMKClusterTapListener.h>

NS_ASSUME_NONNULL_BEGIN

@interface ClusteredYamapView: YamapView<YMKClusterListener, YMKClusterTapListener>

- (void)setClusterColor:(UIColor*_Nullable)color;
- (void)setClusteredMarkers:(NSArray<YMKRequestPoint*>*_Nonnull)points;

@end

NS_ASSUME_NONNULL_END

#endif /* ClusteredYamapView_h */
