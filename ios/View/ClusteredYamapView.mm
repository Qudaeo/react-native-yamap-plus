#import "ClusteredYamapView.h"

#import <React/UIView+React.h>

#import <MarkerView.h>

#import <YandexMapsMobile/YMKMap.h>
#import <YandexMapsMobile/YMKMapObjectCollection.h>
#import <YandexMapsMobile/YMKCluster.h>
#import <YandexMapsMobile/YMKClusterizedPlacemarkCollection.h>
#import <YandexMapsMobile/YMKIconStyle.h>
#import <YandexMapsMobile/YMKDrivingRouter.h>
#import <YandexMapsMobile/YMKMasstransitRouter.h>
#import <YandexMapsMobile/YMKPedestrianRouter.h>

@implementation ClusteredYamapView {
    YMKMasstransitSession *masstransitSession;
    YMKMasstransitSession *walkSession;
    YMKMasstransitRouter *masstransitRouter;
    YMKDrivingRouter* drivingRouter;
    YMKDrivingSession* drivingSession;
    YMKPedestrianRouter *pedestrianRouter;
    YMKTransitOptions *transitOptions;
    YMKMasstransitSessionRouteHandler routeHandler;
    NSMutableArray<UIView*>* _reactSubviews;
    NSMutableArray *routes;
    NSMutableArray *currentRouteInfo;
    NSMutableArray<YMKRequestPoint *>* lastKnownRoutePoints;
    YMKUserLocationView* userLocationView;
    NSMutableDictionary *vehicleColors;
    UIImage* userLocationImage;
    NSArray *acceptVehicleTypes;
    YMKUserLocationLayer *userLayer;
    UIColor* userLocationAccuracyFillColor;
    UIColor* userLocationAccuracyStrokeColor;
    float userLocationAccuracyStrokeWidth;
    YMKClusterizedPlacemarkCollection *clusterCollection;
    UIColor* clusterColor;
    NSMutableArray<YMKPlacemarkMapObject *>* placemarks;
    BOOL userClusters;
    BOOL mapLoaded;
    Boolean initializedRegion;
}

- (instancetype)init {
    self = [super init];
    _reactSubviews = [[NSMutableArray alloc] init];
    placemarks = [[NSMutableArray alloc] init];
    clusterColor=nil;
    userClusters=NO;
    clusterCollection = [[self getMapView].mapWindow.map.mapObjects addClusterizedPlacemarkCollectionWithClusterListener:self];
    initializedRegion = NO;
    mapLoaded = NO;
    return self;
}

- (void)setClusteredMarkers:(NSArray<YMKPoint*>*) markers {
    [placemarks removeAllObjects];
    [clusterCollection clear];
    NSArray<YMKPlacemarkMapObject *>* newPlacemarks = [clusterCollection addPlacemarksWithPoints:markers image:[self clusterImage:[NSNumber numberWithFloat:[markers count]]] style:[YMKIconStyle new]];
    [placemarks addObjectsFromArray:newPlacemarks];
    for (int i=0; i<[placemarks count]; i++) {
        if (i<[_reactSubviews count]) {
            UIView *subview = [_reactSubviews objectAtIndex:i];
            if ([subview isKindOfClass:[MarkerView class]]) {
                MarkerView *marker = (MarkerView*) subview;
                [marker setClusterMapObject:[placemarks objectAtIndex:i]];
            }
        }
    }
    if (mapLoaded) {
        [self clusterPlacemarks];
    }
}

- (void) clusterPlacemarks {
    [clusterCollection clusterPlacemarksWithClusterRadius:50 minZoom:12];
}

- (RCTBubblingEventBlock)onMapLoaded {
    mapLoaded = YES;
    [self clusterPlacemarks];
    return [super onMapLoaded];
}

- (void)setClusterColor: (UIColor*) color {
    clusterColor = [RCTConvert UIColor:color];
}

- (void)onObjectRemovedWithView:(nonnull YMKUserLocationView *) view {
}

- (void)onMapTapWithMap:(nonnull YMKMap *) map
                  point:(nonnull YMKPoint *) point {
    if (self.onMapPress) {
        NSDictionary* data = @{
            @"lat": [NSNumber numberWithDouble:point.latitude],
            @"lon": [NSNumber numberWithDouble:point.longitude],
        };
        self.onMapPress(data);
    }
}

- (void)onMapLongTapWithMap:(nonnull YMKMap *) map
                      point:(nonnull YMKPoint *) point {
    if (self.onMapLongPress) {
        NSDictionary* data = @{
            @"lat": [NSNumber numberWithDouble:point.latitude],
            @"lon": [NSNumber numberWithDouble:point.longitude],
        };
        self.onMapLongPress(data);
    }
}

// children
- (void)addSubview:(UIView *) view {
    [super addSubview:view];
}

- (void)insertReactSubview:(UIView<RCTComponent>*) subview atIndex:(NSInteger) atIndex {
     if ([subview isKindOfClass:[MarkerView class]]) {
         MarkerView *marker = (MarkerView*) subview;
         if (atIndex<[placemarks count]) {
             [marker setClusterMapObject:[placemarks objectAtIndex:atIndex]];
         }
    }
    [_reactSubviews insertObject:subview atIndex:atIndex];
    [super insertMarkerReactSubview:subview atIndex:atIndex];
}

- (void)removeReactSubview:(UIView<RCTComponent>*) subview {
     if ([subview isKindOfClass:[MarkerView class]]) {
         MarkerView *marker = (MarkerView*) subview;
        [clusterCollection removeWithMapObject:[marker getMapObject]];
    } else {
        NSArray<id<RCTComponent>> *childSubviews = [subview reactSubviews];
        for (int i = 0; i < childSubviews.count; i++) {
            [self removeReactSubview:(UIView *)childSubviews[i]];
        }
    }
    [_reactSubviews removeObject:subview];
    [super removeMarkerReactSubview:subview];
}

-(UIImage*)clusterImage:(NSNumber*) clusterSize {
    float FONT_SIZE = 45;
    float MARGIN_SIZE = 9;
    float STROKE_SIZE = 9;
    NSString *text = [clusterSize stringValue];
    UIFont *font = [UIFont systemFontOfSize:FONT_SIZE];
    CGSize size = [text sizeWithAttributes:@{NSFontAttributeName:font}];
    float textRadius = sqrt(size.height * size.height + size.width * size.width) / 2;
    float internalRadius = textRadius + MARGIN_SIZE;
    float externalRadius = internalRadius + STROKE_SIZE;
    // This function returns a newImage, based on image, that has been:
    // - scaled to fit in (CGRect) rect
    // - and cropped within a circle of radius: rectWidth/2

    //Create the bitmap graphics context
    UIGraphicsBeginImageContextWithOptions(CGSizeMake(externalRadius*2, externalRadius*2), NO, 1.0);
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextSetFillColorWithColor(context, [clusterColor CGColor]);
    CGContextFillEllipseInRect(context, CGRectMake(0, 0, externalRadius*2, externalRadius*2));
    CGContextSetFillColorWithColor(context, [UIColor.whiteColor CGColor]);
    CGContextFillEllipseInRect(context, CGRectMake(STROKE_SIZE, STROKE_SIZE, internalRadius*2, internalRadius*2));
    [text drawInRect:CGRectMake(externalRadius - size.width/2, externalRadius - size.height/2, size.width, size.height) withAttributes:@{NSFontAttributeName: font, NSForegroundColorAttributeName: UIColor.blackColor }];
       UIImage *newImage = UIGraphicsGetImageFromCurrentImageContext();
       UIGraphicsEndImageContext();

       return newImage;
}

- (void)onClusterAddedWithCluster:(nonnull YMKCluster *)cluster {
    NSNumber *myNum = @([cluster size]);
    [[cluster appearance] setIconWithImage:[self clusterImage:myNum]];
    [cluster addClusterTapListenerWithClusterTapListener:self];
}

- (BOOL)onClusterTapWithCluster:(nonnull YMKCluster *)cluster {
    NSMutableArray<YMKPoint*>* lastKnownMarkers = [[NSMutableArray alloc] init];
    for (YMKPlacemarkMapObject *placemark in [cluster placemarks]) {
        [lastKnownMarkers addObject:[placemark geometry]];
    }
    [self fitMarkers:lastKnownMarkers];
    return YES;
}

@end
