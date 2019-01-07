var mockNamedSearchFilterGroup1 = {
    id: 1
};

var mockNamedSearchFilterGroup2 = {
    id: 2
};

var mockNamedSearchFilterGroup3 = {
    id: 3
};

var mockNamedSearchFilterGroup = function($q) {
    var model = mockModel($q, mockNamedSearchFilterGroup1);

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

