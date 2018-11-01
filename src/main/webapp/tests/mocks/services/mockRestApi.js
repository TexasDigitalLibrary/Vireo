angular.module('mock.restApi', []).service('RestApi', function ($q) {
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

    service.get = function () {
        defer = $q.defer();
        return defer.promise;
    };

    service.head = function () {
        defer = $q.defer();
        return defer.promise;
    };

    service.post = function () {
        defer = $q.defer();
        return defer.promise;
    };

    service.put = function () {
        defer = $q.defer();
        return defer.promise;
    };

    return service;
});
