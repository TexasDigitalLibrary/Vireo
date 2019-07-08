var dataSavedFilterRepo1 = [
    dataSavedFilter1,
    dataSavedFilter2,
    dataSavedFilter3
];

var dataSavedFilterRepo2 = [
    dataSavedFilter3,
    dataSavedFilter2,
    dataSavedFilter1
];

var dataSavedFilterRepo3 = [
    dataSavedFilter4,
    dataSavedFilter5,
    dataSavedFilter6
];

angular.module("mock.savedFilterRepo", []).service("SavedFilterRepo", function($q) {
    var repo = mockRepo("SavedFilterRepo", $q, mockSavedFilter, dataSavedFilterRepo1);

    return repo;
});
