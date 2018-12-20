angular.module('mock.userService', []).service('UserService', function ($q) {
    var service = mockService($q);
    var currentUser;

    service.mockCurrentUser = function(toMock) {
        delete sessionStorage.role;

        currentUser = angular.extend(mockUser($q), toMock);

        sessionStorage.role = toMock.role;
    };

    currentuser = service.mockCurrentUser(mockUser1);

    service.fetchUser = function () {
        delete sessionStorage.role;
        sessionStorage.role = currentUser.role;
        return payloadPromise($q.defer(), currentUser);
    };

    service.getCurrentUser = function () {
        return currentUser;
    };

    service.setCurrentUser = function (user) {
        angular.extend(currentUser, user);
    };

    service.userEvents = function () {
        return payloadPromise($q.defer(), null);
    };

    service.userReady = function () {
        return payloadPromise($q.defer(), currentUser);
    };

    return service;
});
