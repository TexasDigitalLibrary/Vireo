var mockManagedConfiguration1 = {
    'id': 1,
    'value': ""
};

var mockManagedConfiguration2 = {
    'id': 2,
    'value': ""
};

var mockManagedConfiguration3 = {
    'id': 3,
    'value': ""
};

angular.module('mock.ManagedConfiguration', []).service('ManagedConfiguration', function($q) {
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
        model.id = toMock.id;
        model.value = toMock.value;
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

    model.reset = function() {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    model.save = function() {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    return model;
});
