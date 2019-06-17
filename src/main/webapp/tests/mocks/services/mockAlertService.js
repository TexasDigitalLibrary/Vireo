angular.module("mock.alertService", []).service("AlertService", function ($q) {
    var service = mockService($q);

    service.add = function (meta, channel) {
        return payloadPromise($q.defer(), null);
    };

    service.addAlertServiceError = function (error) {
        return payloadPromise($q.defer(), null);
    };

    service.clearTypeStores = function () {
        return payloadPromise($q.defer(), null);
    };

    service.create = function (facet, exclusion) {
        return payloadPromise($q.defer(), null);
    };

    service.get = function (facet) {
        return payloadPromise($q.defer(), null);
    };

    service.remove = function (alert) {
        return payloadPromise($q.defer(), null);
    };

    service.removeAll = function (facet) {
        return payloadPromise($q.defer(), null);
    };

    return service;
});
