var mockSavedFilter1 = {
    id: 1
};

var mockSavedFilter2 = {
    id: 2
};

var mockSavedFilter3 = {
    id: 3
};

var mockSavedFilter = function($q) {
    var model = mockModel($q, mockSavedFilter1);

    return model;
};

angular.module('mock.savedFilter', []).service('SavedFilter', mockSavedFilter);

