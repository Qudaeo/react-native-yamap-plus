#import "MarkerView.h"

#import <React/UIView+React.h>

#import "ImageCacheManager.h"

#import <YandexMapsMobile/YMKIconStyle.h>

#ifdef RCT_NEW_ARCH_ENABLED

#import "../Util/NewArchUtils.h"

#import <react/renderer/components/RNYamapPlusSpec/ComponentDescriptors.h>
#import <react/renderer/components/RNYamapPlusSpec/EventEmitters.h>
#import <react/renderer/components/RNYamapPlusSpec/Props.h>
#import <react/renderer/components/RNYamapPlusSpec/RCTComponentViewHelpers.h>

#import "RCTFabricComponentsPlugins.h"

using namespace facebook::react;

@interface MarkerView () <RCTMarkerViewViewProtocol, YMKMapObjectTapListener>

@end

#endif

#define YAMAP_FRAMES_PER_SECOND 25

@implementation MarkerView {
    YMKPoint* _point;
    YMKPlacemarkMapObject *mapObject;
    float zIndex;
    NSNumber *scale;
    NSNumber *rotationType;
    NSString *source;
    NSString *lastSource;
    NSValue *anchor;
    NSNumber *visible;
    BOOL handled;
    NSMutableArray<UIView*> *_reactSubviews;
    YRTViewProvider *_markerViewProvider;
}

- (instancetype)init {
    if (self = [super init]) {

#ifdef RCT_NEW_ARCH_ENABLED

        static const auto defaultProps = std::make_shared<const MarkerViewProps>();
        _props = defaultProps;

#endif

        zIndex = 1;
        scale = [NSNumber numberWithInt:1];
        rotationType = [NSNumber numberWithInt:0];
        visible = [NSNumber numberWithInt:1];
        handled = NO;
        _reactSubviews = [[NSMutableArray alloc] init];
        source = @"";
        lastSource = @"";
    }

    return self;
}

#ifdef RCT_NEW_ARCH_ENABLED

+ (ComponentDescriptorProvider)componentDescriptorProvider
{
    return concreteComponentDescriptorProvider<MarkerViewComponentDescriptor>();
}

- (void)updateProps:(const Props::Shared &)props oldProps:(const Props::Shared &)oldProps {
    const auto &oldViewProps = *std::static_pointer_cast<MarkerViewProps const>(_props);
    const auto &newViewProps = *std::static_pointer_cast<MarkerViewProps const>(props);

    if (oldViewProps.point.lat != newViewProps.point.lat || oldViewProps.point.lon != newViewProps.point.lon) {
        _point = [YMKPoint pointWithLatitude:newViewProps.point.lat longitude:newViewProps.point.lon];
    }

    if (oldViewProps.source != newViewProps.source) {
        source = [NSString stringWithCString:newViewProps.source.c_str() encoding:[NSString defaultCStringEncoding]];
    }

    if (oldViewProps.anchor.x != newViewProps.anchor.x || oldViewProps.anchor.x != newViewProps.anchor.x) {
        anchor = [NSValue valueWithCGPoint:CGPointMake(newViewProps.anchor.x, newViewProps.anchor.y)];
    }

    if (oldViewProps.scale != newViewProps.scale) {
        scale = [NSNumber numberWithFloat:newViewProps.scale];
    }

    if (oldViewProps.visible != newViewProps.visible) {
        visible = [NSNumber numberWithInt:newViewProps.visible];
    }

    if (oldViewProps.rotated != newViewProps.rotated) {
        rotationType = [NSNumber numberWithInt:newViewProps.rotated];
    }

    if (oldViewProps.zI != newViewProps.zI) {
        zIndex = newViewProps.zI;
    }

    if (oldViewProps.handled != newViewProps.handled) {
        handled = newViewProps.handled;
    }

    [self updateMarker];
}

- (void)handleCommand:(const NSString *)commandName args:(const NSArray *)args {
    if ([commandName isEqual:@"animatedMoveTo"]) {
        NSDictionary *coords = args[0][0][@"coords"];
        NSNumber *duration = args[0][0][@"duration"];

        [self animatedMoveTo:[YMKPoint pointWithLatitude:[coords[@"lat"] doubleValue] longitude:[coords[@"lon"] doubleValue]] withDuration:[duration floatValue]];
    } else if ([commandName isEqual:@"animatedRotateTo"]) {
        NSNumber *angle = args[0][0][@"angle"];
        NSNumber *duration = args[0][0][@"duration"];

        [self animatedRotateTo:[angle floatValue] withDuration:[duration floatValue]];
    }
}

- (void)prepareForRecycle
{
    [super prepareForRecycle];
    zIndex = 1;
    scale = [NSNumber numberWithInt:1];
    rotationType = [NSNumber numberWithInt:0];
    visible = [NSNumber numberWithInt:1];
    handled = NO;
    source = nil;
    lastSource = nil;
    _reactSubviews = [[NSMutableArray alloc] init];
}

#else

- (void)setScale:(NSNumber*)_scale {
    scale = _scale;
    [self updateMarker];
}
- (void)setRotated:(NSNumber*) rotated {
    rotationType = rotated;
    [self updateMarker];
}

- (void)setZI:(NSNumber *)zI {
    zIndex = [zI floatValue];
    [self updateMarker];
}

- (void)setVisible:(NSNumber*)_visible {
    visible = _visible;
    [self updateMarker];
}

- (void)setHandled:(BOOL)_handled {
    handled = _handled;
}

- (void)setPoint:(YMKPoint*)point {
    _point = point;
    [self updateMarker];
}

- (void)setSource:(NSString*)_source {
    source = _source;
    [self updateMarker];
}

- (void)setAnchor:(NSValue*)_anchor {
    anchor = _anchor;
}

#endif

- (BOOL)onMapObjectTapWithMapObject:(nonnull YMKMapObject*)_mapObject point:(nonnull YMKPoint*)point {

#ifdef RCT_NEW_ARCH_ENABLED

    std::dynamic_pointer_cast<const MarkerViewEventEmitter>(_eventEmitter)->onPress({});

#else

    if (self.onPress)
        self.onPress(@{});

#endif

    return handled;
}

- (void)updateMarker {
    if (mapObject != nil && [mapObject isValid]) {
        [mapObject setGeometry:_point];
        [mapObject setZIndex:zIndex];
        YMKIconStyle* iconStyle = [[YMKIconStyle alloc] init];
        [iconStyle setScale:scale];
        [iconStyle setVisible:visible];
        if (anchor) {
          [iconStyle setAnchor:anchor];
        }
        [iconStyle setRotationType:rotationType];
        if ([_reactSubviews count] == 0) {
            if (source != nil && ![source isEqualToString:@""] && ![source isEqual:lastSource]) {
                [[ImageCacheManager instance] getWithSource:source completion:^(UIImage *image) {
                    if ([self->mapObject isValid]) {
                        [self->mapObject setIconWithImage:image];
                        self->lastSource = self->source;
                        [self updateMarker];
                    }
                }];
            }
        }

        if (_markerViewProvider == nil) {
            [mapObject setIconStyleWithStyle:iconStyle];
        } else {
            [mapObject setViewWithView:_markerViewProvider style:iconStyle];
        }
    }
}

- (void)updateClusterMarker {
    if (mapObject != nil && [mapObject isValid]) {
        [mapObject setGeometry:_point];
        [mapObject setZIndex:zIndex];
        YMKIconStyle* iconStyle = [[YMKIconStyle alloc] init];
        [iconStyle setScale:scale];
        [iconStyle setVisible:visible];
        if (anchor) {
          [iconStyle setAnchor:anchor];
        }
        [iconStyle setRotationType:rotationType];
        if ([_reactSubviews count] == 0) {
            if (source != nil && ![source isEqualToString:@""] && ![source isEqual:lastSource]) {
                [[ImageCacheManager instance] getWithSource:source completion:^(UIImage *image) {
                    if ([self->mapObject isValid]) {
                        [self->mapObject setIconWithImage:image];
                        self->lastSource = self->source;
                        [self updateClusterMarker];
                    }
                }];
            }
        }
        [mapObject setIconStyleWithStyle:iconStyle];
    }
}

- (YMKPoint*)getPoint {
    return _point;
}

- (YMKPlacemarkMapObject*)getMapObject {
    return mapObject;
}

- (void)setMapObject:(YMKPlacemarkMapObject *)_mapObject {
    mapObject = _mapObject;
    if ([mapObject isValid]) {
        [mapObject addTapListenerWithTapListener:self];
    }
    [self updateMarker];
}

- (void)setClusterMapObject:(YMKPlacemarkMapObject *)_mapObject {
    mapObject = _mapObject;
    if ([mapObject isValid]) {
        [mapObject addTapListenerWithTapListener:self];
    }
    [self updateClusterMarker];
}

- (void)setChildView {
    if ([_reactSubviews count] > 0) {
        UIView *_childView = [_reactSubviews objectAtIndex:0];
        if (_childView != nil) {
            [_childView setOpaque:false];
            _markerViewProvider = [[YRTViewProvider alloc] initWithUIView:_childView];
            [self updateMarker];
        }
    } else {
        _markerViewProvider = nil;
    }
}

#ifdef RCT_NEW_ARCH_ENABLED

- (void)mountChildComponentView:(UIView<RCTComponentViewProtocol> *)childComponentView index:(NSInteger)index {
    [_reactSubviews insertObject:childComponentView atIndex:index];
    [self setChildView];
}

- (void)unmountChildComponentView:(UIView<RCTComponentViewProtocol> *)childComponentView index:(NSInteger)index {
    [childComponentView removeFromSuperview];
}

#else

- (void)didUpdateReactSubviews {
    dispatch_async(dispatch_get_main_queue(), ^{
        [self setChildView];
    });
}

- (void)insertReactSubview:(UIView*)subview atIndex:(NSInteger)atIndex {
    [_reactSubviews insertObject:subview atIndex: atIndex];
    [super insertReactSubview:subview atIndex:atIndex];
}

- (void)removeReactSubview:(UIView*)subview {
    [_reactSubviews removeObject:subview];
    [super removeReactSubview: subview];
}

#endif

- (void)moveAnimationLoop:(NSInteger)frame withTotalFrames:(NSInteger)totalFrames withDeltaLat:(double)deltaLat withDeltaLon:(double)deltaLon {
    @try  {
        YMKPlacemarkMapObject *placemark = [self getMapObject];
        YMKPoint* p = placemark.geometry;
        placemark.geometry = [YMKPoint pointWithLatitude:p.latitude + deltaLat/totalFrames
                                            longitude:p.longitude + deltaLon/totalFrames];

        if (frame < totalFrames) {
            dispatch_after(dispatch_time(DISPATCH_TIME_NOW, NSEC_PER_SEC / YAMAP_FRAMES_PER_SECOND), dispatch_get_main_queue(), ^{
                [self moveAnimationLoop: frame+1 withTotalFrames:totalFrames withDeltaLat:deltaLat withDeltaLon:deltaLon];
            });
        }
    } @catch (NSException *exception) {
        NSLog(@"Reason: %@ ",exception.reason);
    }
}

- (void)rotateAnimationLoop:(NSInteger)frame withTotalFrames:(NSInteger)totalFrames withDelta:(double)delta {
    @try  {
        YMKPlacemarkMapObject *placemark = [self getMapObject];
        [placemark setDirection:placemark.direction+(delta / totalFrames)];

        if (frame < totalFrames) {
            dispatch_after(dispatch_time(DISPATCH_TIME_NOW, NSEC_PER_SEC / YAMAP_FRAMES_PER_SECOND), dispatch_get_main_queue(), ^{
                [self rotateAnimationLoop: frame+1 withTotalFrames:totalFrames withDelta:delta];
            });
        }
    } @catch (NSException *exception) {
        NSLog(@"Reason: %@ ",exception.reason);
    }
}

- (void)animatedMoveTo:(YMKPoint*)point withDuration:(float)duration {
    @try  {
        YMKPlacemarkMapObject* placemark = [self getMapObject];
        YMKPoint* p = placemark.geometry;
        double deltaLat = point.latitude - p.latitude;
        double deltaLon = point.longitude - p.longitude;
        [self moveAnimationLoop: 1 withTotalFrames:[@(duration / YAMAP_FRAMES_PER_SECOND) integerValue] withDeltaLat:deltaLat withDeltaLon:deltaLon];
    } @catch (NSException *exception) {
        NSLog(@"Marker animatedMoveTo error: %@",exception.reason);
    }
}

- (void)animatedRotateTo:(float)angle withDuration:(float)duration {
    @try  {
        YMKPlacemarkMapObject* placemark = [self getMapObject];
        double delta = angle - placemark.direction;
        [self rotateAnimationLoop: 1 withTotalFrames:[@(duration / YAMAP_FRAMES_PER_SECOND) integerValue] withDelta:delta];
    } @catch (NSException *exception) {
        NSLog(@"Marker animatedRotateTo error: %@",exception.reason);
    }
}

@end
