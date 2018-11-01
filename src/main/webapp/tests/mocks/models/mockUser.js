var mockUser1 = {
    "lastName": "Daniels",
    "firstName": "Jack",
    "uin": "123456789",
    "exp": "1425393875282",
    "email": "aggieJack@library.tamu.edu",
    "role": "ROLE_ADMIN",
    "netId": "aggieJack"
};

var mockUser2 = {
    "lastName": "Daniels",
    "firstName": "Jill",
    "uin": "987654321",
    "exp": "1425393875282",
    "email": "aggieJill@library.tamu.edu",
    "role": "ROLE_USER",
    "netId": "aggieJill"
};

var mockUser3 = {
    "lastName": "Smith",
    "firstName": "Jacob",
    "uin": "192837465",
    "exp": "1425393875282",
    "email": "jsmith@library.tamu.edu",
    "role": "ROLE_USER",
    "netId": "jsmith"
};

angular.module('mock.user', []).service('User', function ($q) {
    var model = this;
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

    model.isDirty = false;

    model.mock = function(toMock) {
        model.lastName = toMock.lastName;
        model.firstName = toMock.firstName;
        model.uin = toMock.uin;
        model.exp = toMock.exp;
        model.email = toMock.email;
        model.role = toMock.role;
        model.netId = toMock.netId;
    };

    model.clearValidationResults = function () {
    };

    model.delete = function() {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    model.dirty = function(boolean) {
        model.isDirty = boolean;
    };

    model.reload = function() {
    };

    model.save = function() {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    return model;
});
