var mockCustomActionValue1 = {
    'id': 1
};

var mockCustomActionValue2 = {
    'id': 2
};

var mockCustomActionValue3 = {
    'id': 3
};

var mockCustomActionValue = function($q) {
    var model = mockModel($q, mockCustomActionValue1);

    return model;
};

angular.module('mock.customActionValue', []).service('CustomActionValue', mockCustomActionValue);

