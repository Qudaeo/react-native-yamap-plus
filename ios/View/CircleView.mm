#import "CircleView.h"

#import "../Util/RCTConvert+Yamap.mm"

@implementation CircleView {
    YMKPoint* center;
    float radius;
    YMKCircleMapObject* mapObject;
    YMKCircle* circle;
    UIColor* fillColor;
    UIColor* strokeColor;
    NSNumber* strokeWidth;
    NSNumber* zIndex;
    BOOL handled;
}

- (instancetype)init {
    self = [super init];
    fillColor = UIColor.blackColor;
    strokeColor = UIColor.blackColor;
    zIndex = [[NSNumber alloc] initWithInt:1];
    strokeWidth = [[NSNumber alloc] initWithInt:1];
    handled = NO;
    center = [YMKPoint pointWithLatitude:0 longitude:0];
    radius = 0.f;
    circle = [YMKCircle circleWithCenter:center radius:radius];

    return self;
}

- (void)updateCircle {
    if (mapObject != nil) {
        [mapObject setGeometry:circle];
        [mapObject setZIndex:[zIndex floatValue]];
        [mapObject setFillColor:fillColor];
        [mapObject setStrokeColor:strokeColor];
        [mapObject setStrokeWidth:[strokeWidth floatValue]];
    }
}

- (void)setFillColor:(NSNumber*)color {
    fillColor = [RCTConvert UIColor:color];
    [self updateCircle];
}

- (void)setStrokeColor:(NSNumber*)color {
    strokeColor = [RCTConvert UIColor:color];
    [self updateCircle];
}

- (void)setStrokeWidth:(NSNumber*)width {
    strokeWidth = width;
    [self updateCircle];
}

- (void)setZI:(NSNumber*)_zIndex {
    zIndex = _zIndex;
    [self updateCircle];
}

- (void)setHandled:(BOOL)_handled {
    handled = _handled;
}

- (void)updateGeometry {
    if (center) {
        circle = [YMKCircle circleWithCenter:center radius:radius];
    }
}

- (void)setCircleCenter:(YMKPoint*)point {
    center = point;
    [self updateGeometry];
    [self updateCircle];
}

- (void)setRadius:(float)_radius {
    radius = _radius;
    [self updateGeometry];
    [self updateCircle];
}

- (void)setMapObject:(YMKCircleMapObject*)_mapObject {
    mapObject = _mapObject;
    [mapObject addTapListenerWithTapListener:self];
    [self updateCircle];
}

- (YMKCircle*)getCircle {
    return circle;
}

- (BOOL)onMapObjectTapWithMapObject:(nonnull YMKMapObject*)mapObject point:(nonnull YMKPoint*)point {
    if (self.onPress)
        self.onPress(@{});

    return handled;
}

- (YMKCircleMapObject*)getMapObject {
    return mapObject;
}

@end
