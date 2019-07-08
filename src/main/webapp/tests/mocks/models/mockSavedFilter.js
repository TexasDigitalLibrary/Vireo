var dataSavedFilter1 = {
    id: 1,
    name: "filter 1",
    submissionListColumn: {},
    allColumnSearch: true,
    exactMatch: true
};

var dataSavedFilter2 = {
    id: 2,
    name: "filter 2",
    submissionListColumn: {},
    allColumnSearch: false,
    exactMatch: true
};

var dataSavedFilter3 = {
    id: 3,
    name: "filter 3",
    submissionListColumn: {},
    allColumnSearch: true,
    exactMatch: false
};

var dataSavedFilter4 = {
    id: 4,
    name: "filter 4",
    submissionListColumn: {},
    allColumnSearch: false,
    exactMatch: false
};

var dataSavedFilter5 = {
    id: 5,
    name: "filter 5",
    submissionListColumn: {},
    allColumnSearch: true,
    exactMatch: false
};

var dataSavedFilter6 = {
    id: 6,
    name: "filter 6",
    submissionListColumn: {},
    allColumnSearch: false,
    exactMatch: true
};

var mockSavedFilter = function($q) {
    var model = mockModel("SavedFilter", $q, dataSavedFilter1);

    return model;
};

angular.module("mock.savedFilter", []).service("SavedFilter", mockSavedFilter);

