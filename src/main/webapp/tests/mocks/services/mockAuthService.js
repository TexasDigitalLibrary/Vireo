angular.module('mock.authService', []).service('AuthService', function ($q) {
    var service = mockService($q);

    return service;
});
