angular.module('mock.wsApi', []).service('WsApi', function ($q) {
    var service = this;
    var defer;

    var payloadResponse = function (payload) {
        return defer.resolve({
            body: angular.toJson({
                meta: {
                    status: 'SUCCESS'
                },
                payload: payload
            })
        });
    };

    var messageResponse = function (message) {
        return defer.resolve({
            body: angular.toJson({
                meta: {
                    status: 'SUCCESS',
                    message: message
                }
            })
        });
    };

    service.fetch = function (apiReq) {
        defer = $q.defer();
        return defer.promise;
    };

    service.listen = function (apiReq) {
        defer = $q.defer();
        return defer.promise;
    };

    return service;
});
