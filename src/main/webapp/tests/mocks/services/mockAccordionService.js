angular.module('mock.accordionService', []).service('AccordionService', function ($q) {
    var service = mockService($q);

    service.closeAll = function () {
        return payloadPromise($q.defer());
    };

    return service;
});
