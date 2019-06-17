angular.module("mock.validationStore", []).service("ValidationStore", function ($q) {
    var service = mockService($q);

    return service;
});
