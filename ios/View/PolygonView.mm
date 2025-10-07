#import "PolygonView.h"

#import "../Util/RCTConvert+Yamap.mm"
#import "../Util/NewArchUtils.h"

#import <react/renderer/components/RNYamapPlusSpec/ComponentDescriptors.h>
#import <react/renderer/components/RNYamapPlusSpec/EventEmitters.h>
#import <react/renderer/components/RNYamapPlusSpec/Props.h>
#import <react/renderer/components/RNYamapPlusSpec/RCTComponentViewHelpers.h>

#import "RCTFabricComponentsPlugins.h"

using namespace facebook::react;

@interface PolygonView () <RCTPolygonViewViewProtocol, YMKMapObjectTapListener>

@end

@implementation PolygonView {
    NSMutableArray<YMKPoint*>* _points;
    NSArray<NSArray<YMKPoint*>*>* innerRings;
    YMKPolygonMapObject* mapObject;
    YMKPolygon* polygon;
    UIColor* fillColor;
    UIColor* strokeColor;
    float strokeWidth;
    float zIndex;
    BOOL handled;
}

- (instancetype)init {
    if (self = [super init]) {
        static const auto defaultProps = std::make_shared<const PolygonViewProps>();
        _props = defaultProps;

        fillColor = UIColor.blackColor;
        strokeColor = UIColor.blackColor;
        zIndex = 1;
        handled = NO;
        strokeWidth = 0;
        _points = [[NSMutableArray alloc] init];
        innerRings = [[NSMutableArray alloc] init];
        polygon = [YMKPolygon polygonWithOuterRing:[YMKLinearRing linearRingWithPoints:@[]] innerRings:@[]];
    }

    return self;
}

+ (ComponentDescriptorProvider)componentDescriptorProvider
{
    return concreteComponentDescriptorProvider<PolygonViewComponentDescriptor>();
}

- (void)updateProps:(const Props::Shared &)props oldProps:(const Props::Shared &)oldProps {
    const auto &oldViewProps = *std::static_pointer_cast<PolygonViewProps const>(_props);
    const auto &newViewProps = *std::static_pointer_cast<PolygonViewProps const>(props);

    if (![NewArchUtils polygonPointsEquals:oldViewProps.points points2:newViewProps.points]) {
        std::vector<PolygonViewPointsStruct> newPointsStruct = newViewProps.points;
        NSMutableArray<YMKPoint*> *newPointsArray = [[NSMutableArray alloc] init];

        for (int i = 0; i < newPointsStruct.size(); i++) {
            PolygonViewPointsStruct newPointStruct = newPointsStruct.at(i);
            [newPointsArray addObject:[YMKPoint pointWithLatitude:newPointStruct.lat longitude:newPointStruct.lon]];
        }

        _points = newPointsArray;
        polygon = [YMKPolygon polygonWithOuterRing:[YMKLinearRing linearRingWithPoints:newPointsArray] innerRings:@[]];
    }
    
    if (![NewArchUtils polygonInnerRingsEquals:oldViewProps.innerRings innerRings2:newViewProps.innerRings]) {
        std::vector<std::vector<PolygonViewInnerRingsStruct>> newInnerRingsStruct = newViewProps.innerRings;
        NSMutableArray *newInnerRings = [[NSMutableArray alloc] init];

        for (int i = 0; i < newInnerRingsStruct.size(); i++) {
            std::vector<PolygonViewInnerRingsStruct> newInnerRingsVectorStruct = newInnerRingsStruct.at(i);
            NSMutableArray<YMKPoint*> *newPointsArray = [[NSMutableArray alloc] init];
            for (int j = 0; j < newInnerRingsVectorStruct.size(); j++) {
                PolygonViewInnerRingsStruct newInnerRingsStruct = newInnerRingsVectorStruct.at(j);
                [newPointsArray addObject:[YMKPoint pointWithLatitude:newInnerRingsStruct.lat longitude:newInnerRingsStruct.lon]];
            }
            [newInnerRings addObject:[YMKLinearRing linearRingWithPoints:newPointsArray]];
        }
        
        innerRings = newInnerRings;
        polygon = [YMKPolygon polygonWithOuterRing:[YMKLinearRing linearRingWithPoints:_points] innerRings:newInnerRings];
    }

    if (oldViewProps.fillColor != newViewProps.fillColor) {
        fillColor = [RCTConvert UIColor:[NSNumber numberWithInt:newViewProps.fillColor]];
    }

    if (oldViewProps.strokeColor != newViewProps.strokeColor) {
        strokeColor = [RCTConvert UIColor:[NSNumber numberWithInt:newViewProps.strokeColor]];
    }

    if (oldViewProps.strokeWidth != newViewProps.strokeWidth) {
        strokeWidth = newViewProps.strokeWidth;
    }

    if (oldViewProps.zI != newViewProps.zI) {
        zIndex = newViewProps.zI;
    }

    if (oldViewProps.handled != newViewProps.handled) {
        handled = newViewProps.handled;
    }

    [self updatePolygon];
}

- (void)prepareForRecycle
{
  [super prepareForRecycle];
  fillColor = UIColor.blackColor;
  strokeColor = UIColor.blackColor;
  zIndex = 1;
  handled = NO;
  strokeWidth = 0;
  innerRings = [[NSMutableArray alloc] init];
}

- (void)updatePolygon {
    if (mapObject != nil && [mapObject isValid]) {
        [mapObject setGeometry:polygon];
        [mapObject setZIndex:zIndex];
        [mapObject setFillColor:fillColor];
        [mapObject setStrokeColor:strokeColor];
        [mapObject setStrokeWidth:strokeWidth];
    }
}

- (void)setMapObject:(YMKPolygonMapObject *)_mapObject {
    mapObject = _mapObject;
    [mapObject addTapListenerWithTapListener:self];
    [self updatePolygon];
}

- (BOOL)onMapObjectTapWithMapObject:(nonnull YMKMapObject*)mapObject point:(nonnull YMKPoint*)point {
    if (_eventEmitter) {
        std::dynamic_pointer_cast<const PolygonViewEventEmitter>(_eventEmitter)->onPress({});
    }

    return handled;
}

- (NSMutableArray<YMKPoint*>*)getPoints {
    return _points;
}

- (YMKPolygon*)getPolygon {
    return polygon;
}

- (YMKPolygonMapObject*)getMapObject {
    return mapObject;
}

@end
