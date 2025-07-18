#import <React/RCTComponent.h>
#import <React/UIView+React.h>

#import <MapKit/MapKit.h>
#import "../Converter/RCTConvert+Yamap.m"
@import YandexMapsMobile;

#ifndef MAX
#import <NSObjCRuntime.h>
#endif

#import "RNCYMView.h"
#import <YamapMarkerView.h>
#import "ImageCacheManager.h"

#define ANDROID_COLOR(c) [UIColor colorWithRed:((c>>16)&0xFF)/255.0 green:((c>>8)&0xFF)/255.0 blue:((c)&0xFF)/255.0  alpha:((c>>24)&0xFF)/255.0]

#define UIColorFromRGB(rgbValue) [UIColor colorWithRed:((float)((rgbValue & 0xFF0000) >> 16))/255.0 green:((float)((rgbValue & 0xFF00) >> 8))/255.0 blue:((float)(rgbValue & 0xFF))/255.0 alpha:1.0]

@implementation RNCYMView {
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
    UIImage* clusImage;
    CGFloat clusterWidth;
    CGFloat clusterHeight;
    UIColor* clusterTextColor;
    double clusterTextSize;
    double clusterTextYOffset;
    double clusterTextXOffset;
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
    clusterCollection = [self.mapWindow.map.mapObjects addClusterizedPlacemarkCollectionWithClusterListener:self];
    initializedRegion = NO;
    mapLoaded = NO;
    clusterWidth = 32;
    clusterHeight = 32;
    clusterTextColor = UIColor.blackColor;
    clusterTextSize = 45;
    clusterTextYOffset = 0;
    clusterTextXOffset = 0;
    return self;
}

- (void)setClusterIcon:(NSString *)source {
    [[ImageCacheManager instance] getWithSource:source completion:^(UIImage *image) {
        self->clusImage = image;
    }];
}

- (void)setClusterSize:(NSDictionary *)sizes {
    clusterWidth = [sizes valueForKey:@"width"] != nil ? [RCTConvert NSUInteger:sizes[@"width"]] : 0;
    clusterHeight = [sizes valueForKey:@"height"] != nil ? [RCTConvert NSUInteger:sizes[@"height"]] : 0;
}

- (void)setClusterTextColor:(UIColor *)color {
    clusterTextColor = color;
}

- (void)setClusterTextSize:(double)size {
    clusterTextSize = size;
}

- (void)setClusterTextYOffset:(double)offset {
    clusterTextYOffset = offset;
}

- (void)setClusterTextXOffset:(double)offset {
    clusterTextXOffset = offset;
}

- (void)setClusteredMarkers:(NSArray*) markers {
    [placemarks removeAllObjects];
    [clusterCollection clear];
    NSMutableArray<YMKPoint*> *newMarkers = [NSMutableArray new];
    for (NSDictionary *mark in markers) {
        [newMarkers addObject:[YMKPoint pointWithLatitude:[[mark objectForKey:@"lat"] doubleValue] longitude:[[mark objectForKey:@"lon"] doubleValue]]];
    }
    NSArray<YMKPlacemarkMapObject *>* newPlacemarks = [clusterCollection addPlacemarksWithPoints:newMarkers image:[self clusterImage:[NSNumber numberWithFloat:[newMarkers count]]] style:[YMKIconStyle new]];
    [placemarks addObjectsFromArray:newPlacemarks];
    for (int i=0; i<[placemarks count]; i++) {
        if (i<[_reactSubviews count]) {
            UIView *subview = [_reactSubviews objectAtIndex:i];
            if ([subview isKindOfClass:[YamapMarkerView class]]) {
                YamapMarkerView* marker = (YamapMarkerView*) subview;
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
    clusterColor = color;
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

// utils
+ (UIColor*)colorFromHexString:(NSString*) hexString {
    unsigned rgbValue = 0;
    NSScanner *scanner = [NSScanner scannerWithString:hexString];
    [scanner setScanLocation:1];
    [scanner scanHexInt:&rgbValue];
    return [UIColor colorWithRed:((rgbValue & 0xFF0000) >> 16)/255.0 green:((rgbValue & 0xFF00) >> 8)/255.0 blue:(rgbValue & 0xFF)/255.0 alpha:1.0];
}

+ (NSString*)hexStringFromColor:(UIColor *) color {
    const CGFloat *components = CGColorGetComponents(color.CGColor);
    CGFloat r = components[0];
    CGFloat g = components[1];
    CGFloat b = components[2];
    return [NSString stringWithFormat:@"#%02lX%02lX%02lX", lroundf(r * 255), lroundf(g * 255), lroundf(b * 255)];
}

// children
- (void)addSubview:(UIView *) view {
    [super addSubview:view];
}

- (void)insertReactSubview:(UIView<RCTComponent>*) subview atIndex:(NSInteger) atIndex {
    if ([subview isKindOfClass:[YamapMarkerView class]]) {
        YamapMarkerView* marker = (YamapMarkerView*) subview;
        if (atIndex<[placemarks count]) {
            [marker setClusterMapObject:[placemarks objectAtIndex:atIndex]];
        }
    }
    [_reactSubviews insertObject:subview atIndex:atIndex];
    [super insertMarkerReactSubview:subview atIndex:atIndex];
}

- (void)removeReactSubview:(UIView<RCTComponent>*) subview {
    if ([subview isKindOfClass:[YamapMarkerView class]]) {
        YamapMarkerView* marker = (YamapMarkerView*) subview;
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
    NSString *text = [clusterSize stringValue];
    UIFont *font = [UIFont systemFontOfSize:clusterTextSize];
    CGSize size = [text sizeWithAttributes:@{NSFontAttributeName:font}];
    
    // This function returns a newImage, based on image, that has been:
    // - scaled to fit in (CGRect) rect
    // - and cropped within a circle of radius: rectWidth/2
    
    //Create the bitmap graphics context
    if(clusImage && clusterWidth != 0 && clusterHeight != 0) {
        UIGraphicsBeginImageContextWithOptions(CGSizeMake(clusterWidth, clusterHeight), NO, 1.0);
        [clusImage drawInRect:CGRectMake(0, 0, clusterWidth, clusterHeight)];
        [text drawInRect:CGRectMake(clusterWidth / 2  - size.width/2 + clusterTextXOffset, clusterHeight / 2 - size.height/2 + clusterTextYOffset, size.width, size.height) withAttributes:@{NSFontAttributeName: font, NSForegroundColorAttributeName: clusterTextColor }];
    } else {
        float MARGIN_SIZE = 9;
        float STROKE_SIZE = 9;
        
        float textRadius = sqrt(size.height * size.height + size.width * size.width) / 2;
        
        float internalRadius = textRadius + MARGIN_SIZE;
        float externalRadius = internalRadius + STROKE_SIZE;
        
        UIGraphicsBeginImageContextWithOptions(CGSizeMake(externalRadius*2, externalRadius*2), NO, 1.0);
        CGContextRef context = UIGraphicsGetCurrentContext();
        CGContextSetFillColorWithColor(context, [clusterColor CGColor]);
        CGContextFillEllipseInRect(context, CGRectMake(0, 0, externalRadius*2, externalRadius*2));
        CGContextSetFillColorWithColor(context, [UIColor.whiteColor CGColor]);
        CGContextFillEllipseInRect(context, CGRectMake(STROKE_SIZE, STROKE_SIZE, internalRadius*2, internalRadius*2));
        [text drawInRect:CGRectMake(externalRadius - size.width/2, externalRadius - size.height/2, size.width, size.height) withAttributes:@{NSFontAttributeName: font, NSForegroundColorAttributeName: clusterTextColor }];
    }
    
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
    [self fitMarkers:lastKnownMarkers withDuration:1.0 withAnimation:0];
    return YES;
}

@synthesize reactTag;

@end
