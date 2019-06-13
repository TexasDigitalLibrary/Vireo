angular.module("mock.authServiceApi", []).service("AuthServiceApi", function($q) {
    var service = mockService($q);

    return service;
});
