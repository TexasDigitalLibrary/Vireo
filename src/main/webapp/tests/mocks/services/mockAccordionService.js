angular.module('mock.accordionService', []).service('AccordionService', function ($q) {
    var service = mockService($q);

    service.close = function () {
        return payloadPromise($q.defer());
    };

    service.closeAll = function () {
        return payloadPromise($q.defer());
    };

    return service;
});
