#ifdef RCT_NEW_ARCH_ENABLED

#import "NewArchUtils.h"

@implementation NewArchUtils;

+ (BOOL)polylinePointsEquals:(std::vector<PolylineViewPointsStruct>)points1 points2:(std::vector<PolylineViewPointsStruct>)points2 {
    if (points1.size() != points2.size()) {
        return false;
    }

    for (int i = 0; i < points1.size(); i++) {
        PolylineViewPointsStruct p1 = points1.at(i);
        PolylineViewPointsStruct p2 = points2.at(i);

        if ((p1.lat != p2.lat) || (p1.lon != p2.lon)){
            return false;
        }
    }

    return true;
}

+ (BOOL)polygonPointsEquals:(std::vector<PolygonViewPointsStruct>)points1 points2:(std::vector<PolygonViewPointsStruct>)points2 {
    if (points1.size() != points2.size()) {
        return false;
    }

    for (int i = 0; i < points1.size(); i++) {
        PolygonViewPointsStruct p1 = points1.at(i);
        PolygonViewPointsStruct p2 = points2.at(i);

        if ((p1.lat != p2.lat) || (p1.lon != p2.lon)){
            return false;
        }
    }

    return true;
}

+ (BOOL)polygonInnerRingsEquals:(std::vector<std::vector<PolygonViewInnerRingsStruct>>)innerRings1 innerRings2:(std::vector<std::vector<PolygonViewInnerRingsStruct>>)innerRings2 {
    if (innerRings1.size() != innerRings2.size()) {
        return false;
    }
    
    for (int i = 0; i < innerRings1.size(); i++) {
        std::vector<PolygonViewInnerRingsStruct> innerRing1 = innerRings1.at(i);
        std::vector<PolygonViewInnerRingsStruct> innerRing2 = innerRings2.at(i);
        
        if (innerRing1.size() != innerRing2.size()) {
            return false;
        }

        for (int j = 0; j < innerRing1.size(); j++) {
            PolygonViewInnerRingsStruct p1 = innerRing1.at(i);
            PolygonViewInnerRingsStruct p2 = innerRing1.at(i);

            if ((p1.lat != p2.lat) || (p1.lon != p2.lon)){
                return false;
            }
        }
    }
    
    return true;
}

@end

#endif
