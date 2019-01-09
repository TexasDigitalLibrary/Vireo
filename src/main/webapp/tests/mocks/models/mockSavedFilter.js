var dataSavedFilter1 = {
    id: 1
};

var dataSavedFilter2 = {
    id: 2
};

var dataSavedFilter3 = {
    id: 3
};

var dataSavedFilter4 = {
    id: 4
};

var dataSavedFilter5 = {
    id: 5
};

var dataSavedFilter6 = {
    id: 6
};

var mockSavedFilter = function($q) {
    var model = mockModel($q, dataSavedFilter1);

    return model;
};

angular.module('mock.savedFilter', []).service('SavedFilter', mockSavedFilter);

