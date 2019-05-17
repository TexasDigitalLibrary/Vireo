var dataNamedSearchFilter1 = {
    id: 1,
    allColumnSearch: true,
    exactMatch: true,
    filterCriteria: {},
    name: 'named search filter 1'
};

var dataNamedSearchFilter2 = {
    id: 2,
    allColumnSearch: false,
    exactMatch: true,
    filterCriteria: {},
    name: 'named search filter 2'
};

var dataNamedSearchFilter3 = {
    id: 3,
    allColumnSearch: true,
    exactMatch: false,
    filterCriteria: {},
    name: 'named search filter 3'
};

var dataNamedSearchFilter4 = {
    id: 4,
    allColumnSearch: true,
    exactMatch: false,
    filterCriteria: {},
    name: 'named search filter 4'
};

var dataNamedSearchFilter5 = {
    id: 5,
    allColumnSearch: false,
    exactMatch: true,
    filterCriteria: {},
    name: 'named search filter 5'
};

var dataNamedSearchFilter6 = {
    id: 6,
    allColumnSearch: false,
    exactMatch: false,
    filterCriteria: {},
    name: 'named search filter 6'
};

var mockNamedSearchFilter = function($q) {
    var model = mockModel("NamedSearchFilter", $q, dataNamedSearchFilter1);

    return model;
};

angular.module('mock.namedSearchFilter', []).service('NamedSearchFilter', mockNamedSearchFilter);

