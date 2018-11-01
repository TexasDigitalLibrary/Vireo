angular.module('mock.userService', []).service('UserService', function ($q) {
    var service = this;
    var defer;

    var payloadResponse = function (payload) {
        return defer.resolve({
            body: angular.toJson({
                meta: {
                    status: 'SUCCESS'
                },
                payload: payload
            })
        });
    };

    var messageResponse = function (message) {
        return defer.resolve({
            body: angular.toJson({
                meta: {
                    status: 'SUCCESS',
                    message: message
                }
            })
        });
    };

    service.currentUser = {
        clearValidationResults: function () {
        },
        ready: function() {
            return true;
        }
    };

    service.storage = {
        'session': {},
        'local': {}
    };

    service.keys = {
        'session': {},
        'local': {}
    };

    service.set = function (key, value, type) {
        type = (type !== undefined) ? type : appConfig.storageType;
        if (service.keys[type][key] === undefined) {
            service.keys[type][key] = $q.defer();
        }
        service.storage[type][key] = value;
        service.keys[type][key].notify(service.storage[type][key]);
    };

    service.setCurrentUser = function (user) {
        return angular.extend(service.currentUser, user);
    };

    service.get = function (key, type) {
        type = (type !== undefined) ? type : appConfig.storageType;
        return service.storage[type][key];
    };

    service.listen = function (key, type) {
        type = (type !== undefined) ? type : appConfig.storageType;
        if (service.keys[type][key] === undefined) {
            service.keys[type][key] = $q.defer();
        }
        var data = {};
        service.keys[type][key].promise.then(null, null, function (promisedData) {
            angular.extend(data, promisedData);
        });
        return data;
    };

    service.delete = function (key, type) {
        type = (type !== undefined) ? type : appConfig.storageType;
        if (service.keys[type][key] !== undefined) {
            service.keys[type][key].notify(null);
        }
        delete service.keys[type][key];
        delete service.storage[type][key];
    };

    service.getCurrentUser = function () {
        return angular.copy(service.currentUser);
    };

    service.userEvents = function () {
        var payload = {};
        defer = $q.defer();
        payloadResponse(payload);
        return defer.promise;
    };

    service.userReady = function () {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    service.getCurrentUser = function () {
        return angular.copy(service.currentUser);
    };

    for (var type in {'session': '0', 'local': '1'}) {
        for (var key in service.storage[type]) {
            service.keys[type][key] = $q.defer();
            service.keys[type][key].notify(service.storage[type][key]);
            service.set(key, service.storage[type][key], type);
        }
    }

    return service;
});
