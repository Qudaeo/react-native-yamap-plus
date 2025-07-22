#ifndef TransportUtils_h
#define TransportUtils_h

#import <YandexMapsMobile/YMKTransport.h>

@interface TransportUtils : NSObject;

- (void)findRoutes:(NSArray<YMKRequestPoint *> *)_points vehicles:(NSArray<NSString *> *)vehicles withId:(NSString *)_id competition:(void (^)(NSDictionary *response))completion;

@end

#endif
