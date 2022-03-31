angular.module("mock.userService", []).service("UserService", function ($q) {
    var service = mockService($q, mockUser);
    var currentUser;

    service.mockCurrentUser = function(toMock) {
        delete sessionStorage.role;

        if (toMock === undefined || toMock === null) {
            currentUser = null;
        }
        else {
            currentUser = service.mockModel(toMock);
            sessionStorage.role = toMock.role;
        }
    };

    service.mockCurrentUser(dataUser1);

    service.fetchUser = function () {
        delete sessionStorage.role;
        sessionStorage.role = currentUser.role;
        return payloadPromise($q.defer(), currentUser);
    };

    service.getCurrentUser = function () {
        return currentUser;
    };

    service.setCurrentUser = function (user) {
        currentUser = user;
        sessionStorage.role = currentUser.role;
    };

    service.userEvents = function () {
        var defer = $q.defer();
        defer.notify("RECEIVED");
        return payloadPromise(defer);
    };

    service.userReady = function () {
        return payloadPromise($q.defer(), currentUser);
    };

    return service;
});
