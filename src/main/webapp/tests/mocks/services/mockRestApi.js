angular.module('mock.restApi', []).service('RestApi', function ($q) {
    var service = mockService($q);

    service.get = function () {
        return payloadPromise($q.defer(), null);
    };

    service.head = function () {
        return payloadPromise($q.defer(), null);
    };

    service.post = function () {
        return payloadPromise($q.defer(), null);
    };

    service.put = function () {
        return payloadPromise($q.defer(), null);
    };

    return service;
});
