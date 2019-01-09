var dataNamedSearchFilterGroup1 = {
    id: 1
};

var dataNamedSearchFilterGroup2 = {
    id: 2
};

var dataNamedSearchFilterGroup3 = {
    id: 3
};

var mockNamedSearchFilterGroup = function($q) {
    var model = mockModel($q, dataNamedSearchFilterGroup1);

    model.addFilter = function(criterionName, filterValue, filterGloss, exactMatch) {
        return payloadPromise($q.defer());
    };

    model.clearFilters = function() {
        return payloadPromise($q.defer());
    };

    model.removeFilter = function(namedSearchFilterName, filterCriterion) {
        return payloadPromise($q.defer());
    };

    model.set = function(filter) {
        return payloadPromise($q.defer());
    };

    return model;
};

angular.module('mock.namedSearchFilterGroup', []).service('NamedSearchFilterGroup', mockNamedSearchFilterGroup);

