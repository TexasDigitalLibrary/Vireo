var mockFieldProfile1 = {
    'id': 1
};

var mockFieldProfile2 = {
    'id': 2
};

var mockFieldProfile3 = {
    'id': 3
};

var mockFieldProfile = function($q) {
    var model = mockModel($q, mockFieldProfile1);

    return model;
};

angular.module('mock.fieldProfile', []).service('FieldProfile', mockFieldProfile);
