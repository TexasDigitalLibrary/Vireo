angular.module('mock.wsApi', []).service('WsApi', function ($q) {
    var service = mockService($q);
    var mapping;

    service.mockMapping = function(toMock) {
        mapping = {};
        for (var key in toMock) {
            mapping[key] = toMock[key];
        }
    };

    service.fetch = function (apiReq) {
        var payload = {};

        // TODO
        //switch (apiReq.method) {
        //}

        return payloadPromise($q.defer(), payload);
    };

    service.getMapping = function () {
        return mapping;
    };

    service.listen = function (apiReq) {
        return payloadPromise($q.defer());
    };

    return service;
});
