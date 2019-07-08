var dataNamedSearchFilterGroup1 = {
    id: 1,
    columnsFlag: true,
    name: "named search filter group 1",
    namedSearchFilters: [],
    publicFlag: true,
    savedColumns: [],
    umiRelease: true
};

var dataNamedSearchFilterGroup2 = {
    id: 2,
    columnsFlag: false,
    name: "named search filter group 2",
    publicFlag: false,
    namedSearchFilters: [],
    savedColumns: [],
    umiRelease: false
};

var dataNamedSearchFilterGroup3 = {
    id: 3,
    columnsFlag: false,
    name: "named search filter group 3",
    publicFlag: true,
    namedSearchFilters: [],
    savedColumns: [],
    umiRelease: true
};

var dataNamedSearchFilterGroup4 = {
    id: 4,
    columnsFlag: false,
    name: "named search filter group 4",
    publicFlag: true,
    namedSearchFilters: [],
    savedColumns: [],
    umiRelease: false
};

var dataNamedSearchFilterGroup5 = {
    id: 5,
    columnsFlag: true,
    name: "named search filter group 5",
    publicFlag: false,
    namedSearchFilters: [],
    savedColumns: [],
    umiRelease: false
};

var dataNamedSearchFilterGroup6 = {
    id: 6,
    columnsFlag: true,
    name: "named search filter group 6",
    publicFlag: false,
    namedSearchFilters: [],
    savedColumns: [],
    umiRelease: true
};

var mockNamedSearchFilterGroup = function($q) {
    var model = mockModel("NamedSearchFilterGroup", $q, dataNamedSearchFilterGroup1);

    model.addFilter = function(criterionName, filterValue, filterGloss, exactMatch) {
        var found;

        for (var filter in model.namedSearchFilters) {
            if (filter.name === criterionName) {
                found = filter;
                break;
            }
        }

        if (found) {
            return payloadPromise($q.defer(), found);
        }

        var filterCriterion = new mockFilterCriterion($q);
        filterCriterion.criterionName = criterionName;
        filterCriterion.filterValue = filterValue;
        filterCriterion.filterGloss = filterGloss;
        filterCriterion.exactMatch = exactMatch;

        model.namedSearchFilters.push(filterCriterion);

        return payloadPromise($q.defer(), filterCriterion);
    };

    model.clearFilters = function() {
        filter.length = 0;
        return payloadPromise($q.defer());
    };

    model.removeFilter = function(namedSearchFilterName, filterCriterion) {
        for (var i = 0; i < model.namedSearchFilters.length; i++) {
            if (model.namedSearchFilters[i].name === criterionName) {
                delete model.namedSearchFilters[i];
                break;
            }
        }

        return payloadPromise($q.defer());
    };

    model.set = function(filter) {
        return payloadPromise($q.defer());
    };

    return model;
};

angular.module("mock.namedSearchFilterGroup", []).service("NamedSearchFilterGroup", mockNamedSearchFilterGroup);

