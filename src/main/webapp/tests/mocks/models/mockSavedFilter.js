var dataSavedFilter1 = {
    id: 1
};

var dataSavedFilter2 = {
    id: 2
};

var dataSavedFilter3 = {
    id: 3
};

var mockSavedFilter = function($q) {
    var model = mockModel($q, dataSavedFilter1);

    return model;
};

angular.module('mock.savedFilter', []).service('SavedFilter', mockSavedFilter);

