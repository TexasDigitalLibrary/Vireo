var dataSavedFilterRepo1 = [
    dataNamedSearchFilter1,
    dataNamedSearchFilter2,
    dataNamedSearchFilter3
];

var dataSavedFilterRepo2 = [
    dataNamedSearchFilter3,
    dataNamedSearchFilter2,
    dataNamedSearchFilter1
];

var dataSavedFilterRepo3 = [
    dataNamedSearchFilter4,
    dataNamedSearchFilter5,
    dataNamedSearchFilter6
];

angular.module("mock.savedFilterRepo", []).service("SavedFilterRepo", function($q) {
    var repo = mockRepo("SavedFilterRepo", $q, mockNamedSearchFilter, dataSavedFilterRepo1);

    return repo;
});
