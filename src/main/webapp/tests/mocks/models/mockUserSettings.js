var mockUserSettings1 = {
    'id': 1
};

var mockUserSettings2 = {
    'id': 2
};

var mockUserSettings3 = {
    'id': 3
};

var mockUserSettings = function($q) {
    var model = mockModel($q, mockUserSettings1);

    return model;
};

angular.module('mock.userSettings', []).service('UserSettings', mockUserSettings);

