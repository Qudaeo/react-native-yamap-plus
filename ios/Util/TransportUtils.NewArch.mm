#ifdef RCT_NEW_ARCH_ENABLED

#import "TransportUtils.h"
#import "ColorUtil.h"

#import <YandexMapsMobile/YMKDirections.h>
#import <YandexMapsMobile/YMKTransitOptions.h>
#import <YandexMapsMobile/YMKMasstransitRoute.h>
#import <YandexMapsMobile/YMKDrivingRoute.h>
#import <YandexMapsMobile/YMKSubpolylineHelper.h>
#import <YandexMapsMobile/YMKDrivingVehicleOptions.h>

#ifdef RCT_NEW_ARCH_ENABLED

#import <react/renderer/components/RNYamapPlusSpec/ComponentDescriptors.h>
#import <react/renderer/components/RNYamapPlusSpec/EventEmitters.h>
#import <react/renderer/components/RNYamapPlusSpec/Props.h>
#import <react/renderer/components/RNYamapPlusSpec/RCTComponentViewHelpers.h>

#import "RCTFabricComponentsPlugins.h"

using namespace facebook::react;

#endif

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

- (void)findRoutes:(NSArray<YMKRequestPoint *> *)_points vehicles:(NSArray<NSString *> *)vehicles withId:(NSString *)_id competition:(void (^)(YamapViewEventEmitter::OnRouteFound))completion {
    if ([vehicles count] == 1 && [[vehicles objectAtIndex:0] isEqualToString:@"car"]) {
        YMKDrivingOptions *drivingOptions = [[YMKDrivingOptions alloc] init];
        YMKDrivingVehicleOptions *vehicleOptions = [[YMKDrivingVehicleOptions alloc] init];

        drivingSession = [drivingRouter requestRoutesWithPoints:_points drivingOptions:drivingOptions vehicleOptions:vehicleOptions routeHandler:^(NSArray<YMKDrivingRoute *> *routes, NSError *error) {
            if (error != nil) {
                completion({.id = std::string([_id UTF8String]), .status = YamapViewEventEmitter::OnRouteFoundStatus::Error});
                return;
            }

            YamapViewEventEmitter::OnRouteFound response = {.id = std::string([_id UTF8String]), .status = YamapViewEventEmitter::OnRouteFoundStatus::Success};

            for (int i = 0; i < [routes count]; ++i) {
                YMKDrivingRoute *_route = [routes objectAtIndex:i];
                YamapViewEventEmitter::OnRouteFoundRoutes jsonRoute = {.id = std::to_string(i)};
                NSArray<YMKDrivingSection *> *_sections = [_route sections];
                for (int j = 0; j < [_sections count]; ++j) {
                    YamapViewEventEmitter::OnRouteFoundRoutesSections jsonSection = [self convertDrivingRouteSection:_route withSection: [_sections objectAtIndex:j]];
                    jsonRoute.sections.push_back(jsonSection);
                }
                response.routes.push_back(jsonRoute);
            }

            completion(response);
        }];
    }

    YMKMasstransitSessionRouteHandler _routeHandler = ^(NSArray<YMKMasstransitRoute *> *routes, NSError *error) {
        if (error != nil) {
            completion({.id = std::string([_id UTF8String]), .status = YamapViewEventEmitter::OnRouteFoundStatus::Error});
            return;
        }
        YamapViewEventEmitter::OnRouteFound response = {.id = std::string([_id UTF8String]), .status = YamapViewEventEmitter::OnRouteFoundStatus::Success};

        for (int i = 0; i < [routes count]; ++i) {
            YMKMasstransitRoute *_route = [routes objectAtIndex:i];
            YamapViewEventEmitter::OnRouteFoundRoutes jsonRoute = {.id = std::to_string(i)};

            NSArray<YMKMasstransitSection *> *_sections = [_route sections];
            for (int j = 0; j < [_sections count]; ++j) {
                YamapViewEventEmitter::OnRouteFoundRoutesSections jsonSection = [self convertRouteSection:_route withSection: [_sections objectAtIndex:j]];
                jsonRoute.sections.push_back(jsonSection);
            }

            response.routes.push_back(jsonRoute);
        }

        completion(response);
    };

    if ([vehicles count] == 0) {
        walkSession = [pedestrianRouter requestRoutesWithPoints:_points timeOptions:[[YMKTimeOptions alloc] init] routeOptions:routeOptions routeHandler:_routeHandler];
        return;
    }

    YMKTransitOptions *_transitOptions = [YMKTransitOptions transitOptionsWithAvoid:YMKFilterVehicleTypesNone timeOptions:[[YMKTimeOptions alloc] init]];
    masstransitSession = [masstransitRouter requestRoutesWithPoints:_points transitOptions:_transitOptions routeOptions:routeOptions routeHandler:_routeHandler];
}

- (YamapViewEventEmitter::OnRouteFoundRoutesSections)convertDrivingRouteSection:(YMKDrivingRoute*)route withSection:(YMKDrivingSection*)section {
    int routeIndex = 0;
    YMKDrivingWeight *routeWeight = route.metadata.weight;
    YamapViewEventEmitter::OnRouteFoundRoutesSections routeMetadata = {};
    YamapViewEventEmitter::OnRouteFoundRoutesSectionsRouteInfo routeWeightData = {
        .time = std::string([routeWeight.time.text UTF8String]),
        .timeWithTraffic = std::string([routeWeight.timeWithTraffic.text UTF8String]),
        .distance = routeWeight.distance.value
    };
    YamapViewEventEmitter::OnRouteFoundRoutesSectionsSectionInfo sectionWeightData = {
        .time = std::string([section.metadata.weight.time.text UTF8String]),
        .timeWithTraffic = std::string([section.metadata.weight.timeWithTraffic.text UTF8String]),
        .distance = section.metadata.weight.distance.value
    };

    routeMetadata.sectionInfo = sectionWeightData;
    routeMetadata.routeInfo = routeWeightData;
    routeMetadata.routeIndex = routeIndex;
    routeMetadata.stops = {};
    routeMetadata.sectionColor = std::string([[ColorUtil hexStringFromColor:UIColor.darkGrayColor] UTF8String]);

    if (section.metadata.weight.distance.value == 0) {
        routeMetadata.type = "waiting";
    } else {
        routeMetadata.type = "car";
    }

    routeMetadata.transports = {};

    YMKPolyline* subpolyline = [YMKSubpolylineHelper subpolylineWithPolyline:route.geometry subpolyline:section.geometry];

    for (int i = 0; i < [subpolyline.points count]; ++i) {
        YMKPoint* point = [subpolyline.points objectAtIndex:i];
        routeMetadata.points.push_back({
            .lat = point.latitude,
            .lon = point.longitude
        });
    }

    return routeMetadata;
}

- (YamapViewEventEmitter::OnRouteFoundRoutesSections)convertRouteSection:(YMKMasstransitRoute *)route withSection:(YMKMasstransitSection *)section {
    int routeIndex = 0;
    YMKMasstransitWeight* routeWeight = route.metadata.weight;
    YMKMasstransitSectionMetadataSectionData *data = section.metadata.data;
    YamapViewEventEmitter::OnRouteFoundRoutesSections routeMetadata = {};
    double transfersCount = routeWeight.transfersCount;
    YamapViewEventEmitter::OnRouteFoundRoutesSectionsRouteInfo routeWeightData = {
        .time = std::string([routeWeight.time.text UTF8String]),
        .transferCount = transfersCount,
        .walkingDistance = routeWeight.walkingDistance.value
    };
    double sectionTransfersCount = section.metadata.weight.transfersCount;
    YamapViewEventEmitter::OnRouteFoundRoutesSectionsSectionInfo sectionWeightData = {
        .time = std::string([section.metadata.weight.time.text UTF8String]),
        .transferCount = sectionTransfersCount,
        .walkingDistance = section.metadata.weight.walkingDistance.value
    };

    routeMetadata.sectionInfo = sectionWeightData;
    routeMetadata.routeInfo = routeWeightData;
    routeMetadata.routeIndex = routeIndex;

    for (YMKMasstransitRouteStop *stop in section.stops) {
        routeMetadata.stops.push_back(std::string([stop.metadata.stop.name UTF8String]));
    }

    std::vector<std::string> transportTypes = {};

    if (data.transports != nil) {
        for (YMKMasstransitTransport *transport in data.transports) {
            for (NSString *type in transport.line.vehicleTypes) {
                if ([type isEqual: @"suburban"]) continue;
                transportTypes.push_back(std::string([transport.line.name UTF8String]));
                routeMetadata.type = std::string([type UTF8String]);
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
                routeMetadata.sectionColor = std::string([[ColorUtil hexStringFromColor:color] UTF8String]);
            }
        }
    } else {
        routeMetadata.sectionColor = std::string([[ColorUtil hexStringFromColor:UIColor.darkGrayColor] UTF8String]);
        if (section.metadata.weight.walkingDistance.value == 0) {
            routeMetadata.type = "waiting";
        } else {
            routeMetadata.type = "walk";
        }
    }

    routeMetadata.transports.type = transportTypes;

    std::vector<YamapViewEventEmitter::OnRouteFoundRoutesSectionsPoints> points = {};
    YMKPolyline* subpolyline = [YMKSubpolylineHelper subpolylineWithPolyline:route.geometry subpolyline:section.geometry];

    for (int i = 0; i < [subpolyline.points count]; ++i) {
        YMKPoint *point = [subpolyline.points objectAtIndex:i];
        points.push_back({
            .lat = point.latitude,
            .lon = point.longitude
        });
    }

    routeMetadata.points = points;

    return routeMetadata;
}

@end

#endif
