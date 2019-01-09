var dataUserSettings1 = {
    id: 1
};

var dataUserSettings2 = {
    id: 2
};

var dataUserSettings3 = {
    id: 3
};

var dataUserSettings4 = {
    id: 4
};

var dataUserSettings5 = {
    id: 5
};

var dataUserSettings6 = {
    id: 6
};

var mockUserSettings = function($q) {
    var model = mockModel($q, dataUserSettings1);

    return model;
};

angular.module('mock.userSettings', []).service('UserSettings', mockUserSettings);

