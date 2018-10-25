var mockNamedSearchFilterGroup1 = {
    'id': 1
};

var mockNamedSearchFilterGroup2 = {
    'id': 2
};

var mockNamedSearchFilterGroup3 = {
    'id': 3
};

angular.module('mock.NamedSearchFilterGroup', []).service('NamedSearchFilterGroup', function($q) {
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
    };

    model.addFilter = function(criterionName, filterValue, filterGloss, exactMatch) {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    model.clearFilters = function() {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
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

    model.removeFilter = function(namedSearchFilterName, filterCriterion) {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    model.save = function() {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    model.set = function(filter) {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    return model;
});
