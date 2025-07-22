#ifndef RCT_NEW_ARCH_ENABLED

#import "TransportUtils.h"
#import "ColorUtil.h"

#import <YandexMapsMobile/YMKDirections.h>
#import <YandexMapsMobile/YMKTransitOptions.h>
#import <YandexMapsMobile/YMKMasstransitRoute.h>
#import <YandexMapsMobile/YMKDrivingRoute.h>
#import <YandexMapsMobile/YMKSubpolylineHelper.h>
#import <YandexMapsMobile/YMKDrivingVehicleOptions.h>

#define UIColorFromRGB(rgbValue) [UIColor colorWithRed:((float)((rgbValue & 0xFF0000) >> 16))/255.0 green:((float)((rgbValue & 0xFF00) >> 8))/255.0 blue:((float)(rgbValue & 0xFF))/255.0 alpha:1.0]

@implementation TransportUtils {
    YMKMasstransitSession *masstransitSession;
    YMKMasstransitSession *walkSession;
    YMKMasstransitRouter *masstransitRouter;
    YMKDrivingRouter *drivingRouter;
    YMKDrivingSession *drivingSession;
    YMKPedestrianRouter *pedestrianRouter;
    YMKTransitOptions *transitOptions;
    YMKMasstransitSessionRouteHandler routeHandler;
    YMKRouteOptions *routeOptions;
    NSMutableDictionary *vehicleColors;
}

- (instancetype)init
{
    self = [super init];
    if (self) {
        masstransitRouter = [[YMKTransportFactory instance] createMasstransitRouter];
        drivingRouter = [[YMKDirectionsFactory instance] createDrivingRouterWithType: YMKDrivingRouterTypeCombined];
        pedestrianRouter = [[YMKTransportFactory instance] createPedestrianRouter];
        transitOptions = [YMKTransitOptions transitOptionsWithAvoid:YMKFilterVehicleTypesNone timeOptions:[[YMKTimeOptions alloc] init]];
        routeOptions = [YMKRouteOptions routeOptionsWithFitnessOptions:[YMKFitnessOptions fitnessOptionsWithAvoidSteep:false avoidStairs:false]];
        vehicleColors = [[NSMutableDictionary alloc] init];
        [vehicleColors setObject:@"#59ACFF" forKey:@"bus"];
        [vehicleColors setObject:@"#7D60BD" forKey:@"minibus"];
        [vehicleColors setObject:@"#F8634F" forKey:@"railway"];
        [vehicleColors setObject:@"#C86DD7" forKey:@"tramway"];
        [vehicleColors setObject:@"#3023AE" forKey:@"suburban"];
        [vehicleColors setObject:@"#BDCCDC" forKey:@"underground"];
        [vehicleColors setObject:@"#55CfDC" forKey:@"trolleybus"];
        [vehicleColors setObject:@"#2d9da8" forKey:@"walk"];
    }
    return self;
}

- (void)findRoutes:(NSArray<YMKRequestPoint *> *)_points vehicles:(NSArray<NSString *> *)vehicles withId:(NSString *)_id competition:(void (^)(NSDictionary *))completion {
    if ([vehicles count] == 1 && [[vehicles objectAtIndex:0] isEqualToString:@"car"]) {
        YMKDrivingOptions *drivingOptions = [[YMKDrivingOptions alloc] init];
        YMKDrivingVehicleOptions *vehicleOptions = [[YMKDrivingVehicleOptions alloc] init];

        drivingSession = [drivingRouter requestRoutesWithPoints:_points drivingOptions:drivingOptions vehicleOptions:vehicleOptions routeHandler:^(NSArray<YMKDrivingRoute *> *routes, NSError *error) {
            if (error != nil) {
                completion(@{@"id": _id, @"status": @"error"});
                return;
            }

            NSMutableDictionary* response = [[NSMutableDictionary alloc] init];
            [response setValue:_id forKey:@"id"];
            [response setValue:@"status" forKey:@"success"];
            NSMutableArray* jsonRoutes = [[NSMutableArray alloc] init];

            for (int i = 0; i < [routes count]; ++i) {
                YMKDrivingRoute *_route = [routes objectAtIndex:i];
                NSMutableDictionary *jsonRoute = [[NSMutableDictionary alloc] init];
                [jsonRoute setValue:[NSString stringWithFormat:@"%d", i] forKey:@"id"];
                NSMutableArray* sections = [[NSMutableArray alloc] init];
                NSArray<YMKDrivingSection *> *_sections = [_route sections];
                for (int j = 0; j < [_sections count]; ++j) {
                    NSDictionary *jsonSection = [self convertDrivingRouteSection:_route withSection: [_sections objectAtIndex:j]];
                    [sections addObject:jsonSection];
                }
                [jsonRoute setValue:sections forKey:@"sections"];
                [jsonRoutes addObject:jsonRoute];
            }

            [response setValue:jsonRoutes forKey:@"routes"];
            completion(response);
        }];
    }

    YMKMasstransitSessionRouteHandler _routeHandler = ^(NSArray<YMKMasstransitRoute *> *routes, NSError *error) {
        if (error != nil) {
            completion(@{@"id": _id, @"status": @"error"});
            return;
        }
        NSMutableDictionary* response = [[NSMutableDictionary alloc] init];
        [response setValue:_id forKey:@"id"];
        [response setValue:@"success" forKey:@"status"];
        NSMutableArray *jsonRoutes = [[NSMutableArray alloc] init];
        for (int i = 0; i < [routes count]; ++i) {
            YMKMasstransitRoute *_route = [routes objectAtIndex:i];
            NSMutableDictionary *jsonRoute = [[NSMutableDictionary alloc] init];

            [jsonRoute setValue:[NSString stringWithFormat:@"%d", i] forKey:@"id"];
            NSMutableArray *sections = [[NSMutableArray alloc] init];
            NSArray<YMKMasstransitSection *> *_sections = [_route sections];
            for (int j = 0; j < [_sections count]; ++j) {
                NSDictionary *jsonSection = [self convertRouteSection:_route withSection: [_sections objectAtIndex:j]];
                [sections addObject:jsonSection];
            }
            [jsonRoute setValue:sections forKey:@"sections"];
            [jsonRoutes addObject:jsonRoute];
        }
        [response setValue:jsonRoutes forKey:@"routes"];
        completion(response);
    };

    if ([vehicles count] == 0) {
        walkSession = [pedestrianRouter requestRoutesWithPoints:_points timeOptions:[[YMKTimeOptions alloc] init] routeOptions:routeOptions routeHandler:_routeHandler];
        return;
    }

    YMKTransitOptions *_transitOptions = [YMKTransitOptions transitOptionsWithAvoid:YMKFilterVehicleTypesNone timeOptions:[[YMKTimeOptions alloc] init]];
    masstransitSession = [masstransitRouter requestRoutesWithPoints:_points transitOptions:_transitOptions routeOptions:routeOptions routeHandler:_routeHandler];
}

- (NSDictionary*)convertDrivingRouteSection:(YMKDrivingRoute*)route withSection:(YMKDrivingSection*)section {
    int routeIndex = 0;
    YMKDrivingWeight *routeWeight = route.metadata.weight;
    NSMutableDictionary *routeMetadata = [[NSMutableDictionary alloc] init];
    NSMutableDictionary *routeWeightData = [[NSMutableDictionary alloc] init];
    NSMutableDictionary *sectionWeightData = [[NSMutableDictionary alloc] init];
    NSMutableDictionary *transports = [[NSMutableDictionary alloc] init];
    NSMutableArray *stops = [[NSMutableArray alloc] init];
    [routeWeightData setObject:routeWeight.time.text forKey:@"time"];
    [routeWeightData setObject:routeWeight.timeWithTraffic.text forKey:@"timeWithTraffic"];
    [routeWeightData setObject:@(routeWeight.distance.value) forKey:@"distance"];
    [sectionWeightData setObject:section.metadata.weight.time.text forKey:@"time"];
    [sectionWeightData setObject:section.metadata.weight.timeWithTraffic.text forKey:@"timeWithTraffic"];
    [sectionWeightData setObject:@(section.metadata.weight.distance.value) forKey:@"distance"];
    [routeMetadata setObject:sectionWeightData forKey:@"sectionInfo"];
    [routeMetadata setObject:routeWeightData forKey:@"routeInfo"];
    [routeMetadata setObject:@(routeIndex) forKey:@"routeIndex"];
    [routeMetadata setObject:stops forKey:@"stops"];
    [routeMetadata setObject:UIColor.darkGrayColor forKey:@"sectionColor"];

    if (section.metadata.weight.distance.value == 0) {
        [routeMetadata setObject:@"waiting" forKey:@"type"];
    } else {
        [routeMetadata setObject:@"car" forKey:@"type"];
    }

    NSMutableDictionary *wTransports = [[NSMutableDictionary alloc] init];

    for (NSString *key in transports) {
        [wTransports setObject:[transports valueForKey:key] forKey:key];
    }

    [routeMetadata setObject:wTransports forKey:@"transports"];
    NSMutableArray* points = [[NSMutableArray alloc] init];
    YMKPolyline* subpolyline = [YMKSubpolylineHelper subpolylineWithPolyline:route.geometry subpolyline:section.geometry];

    for (int i = 0; i < [subpolyline.points count]; ++i) {
        YMKPoint* point = [subpolyline.points objectAtIndex:i];
        NSMutableDictionary* jsonPoint = [[NSMutableDictionary alloc] init];
        [jsonPoint setValue:[NSNumber numberWithDouble:point.latitude] forKey:@"lat"];
        [jsonPoint setValue:[NSNumber numberWithDouble:point.longitude] forKey:@"lon"];
        [points addObject:jsonPoint];
    }
    [routeMetadata setValue:points forKey:@"points"];

    return routeMetadata;
}

- (NSDictionary *)convertRouteSection:(YMKMasstransitRoute *)route withSection:(YMKMasstransitSection *)section {
    int routeIndex = 0;
    YMKMasstransitWeight* routeWeight = route.metadata.weight;
    YMKMasstransitSectionMetadataSectionData *data = section.metadata.data;
    NSMutableDictionary *routeMetadata = [[NSMutableDictionary alloc] init];
    NSMutableDictionary *routeWeightData = [[NSMutableDictionary alloc] init];
    NSMutableDictionary *sectionWeightData = [[NSMutableDictionary alloc] init];
    NSMutableDictionary *transports = [[NSMutableDictionary alloc] init];
    NSMutableArray *stops = [[NSMutableArray alloc] init];
    [routeWeightData setObject:routeWeight.time.text forKey:@"time"];
    [routeWeightData setObject:@(routeWeight.transfersCount) forKey:@"transferCount"];
    [routeWeightData setObject:@(routeWeight.walkingDistance.value) forKey:@"walkingDistance"];
    [sectionWeightData setObject:section.metadata.weight.time.text forKey:@"time"];
    [sectionWeightData setObject:@(section.metadata.weight.transfersCount) forKey:@"transferCount"];
    [sectionWeightData setObject:@(section.metadata.weight.walkingDistance.value) forKey:@"walkingDistance"];
    [routeMetadata setObject:sectionWeightData forKey:@"sectionInfo"];
    [routeMetadata setObject:routeWeightData forKey:@"routeInfo"];
    [routeMetadata setObject:@(routeIndex) forKey:@"routeIndex"];

    for (YMKMasstransitRouteStop *stop in section.stops) {
        [stops addObject:stop.metadata.stop.name];
    }

    [routeMetadata setObject:stops forKey:@"stops"];

    if (data.transports != nil) {
        for (YMKMasstransitTransport *transport in data.transports) {
            for (NSString *type in transport.line.vehicleTypes) {
                if ([type isEqual: @"suburban"]) continue;
                if (transports[type] != nil) {
                    NSMutableArray *list = transports[type];
                    if (list != nil) {
                        [list addObject:transport.line.name];
                        [transports setObject:list forKey:type];
                    }
                } else {
                    NSMutableArray *list = [[NSMutableArray alloc] init];
                    [list addObject:transport.line.name];
                    [transports setObject:list forKey:type];
                }
                [routeMetadata setObject:type forKey:@"type"];
                UIColor *color;
                if (transport.line.style != nil) {
                    color = UIColorFromRGB([transport.line.style.color integerValue]);
                } else {
                    if ([vehicleColors valueForKey:type] != nil) {
                        color = [ColorUtil colorFromHexString:vehicleColors[type]];
                    } else {
                        color = UIColor.blackColor;
                    }
                }
                [routeMetadata setObject:[ColorUtil hexStringFromColor:color] forKey:@"sectionColor"];
            }
        }
    } else {
        [routeMetadata setObject:UIColor.darkGrayColor forKey:@"sectionColor"];
        if (section.metadata.weight.walkingDistance.value == 0) {
            [routeMetadata setObject:@"waiting" forKey:@"type"];
        } else {
            [routeMetadata setObject:@"walk" forKey:@"type"];
        }
    }

    NSMutableDictionary *wTransports = [[NSMutableDictionary alloc] init];

    for (NSString *key in transports) {
        [wTransports setObject:[transports valueForKey:key] forKey:key];
    }

    [routeMetadata setObject:wTransports forKey:@"transports"];
    NSMutableArray *points = [[NSMutableArray alloc] init];
    YMKPolyline* subpolyline = [YMKSubpolylineHelper subpolylineWithPolyline:route.geometry subpolyline:section.geometry];

    for (int i = 0; i < [subpolyline.points count]; ++i) {
        YMKPoint *point = [subpolyline.points objectAtIndex:i];
        NSMutableDictionary *jsonPoint = [[NSMutableDictionary alloc] init];
        [jsonPoint setValue:[NSNumber numberWithDouble:point.latitude] forKey:@"lat"];
        [jsonPoint setValue:[NSNumber numberWithDouble:point.longitude] forKey:@"lon"];
        [points addObject:jsonPoint];
    }

    [routeMetadata setValue:points forKey:@"points"];

    return routeMetadata;
}

@end

#endif
