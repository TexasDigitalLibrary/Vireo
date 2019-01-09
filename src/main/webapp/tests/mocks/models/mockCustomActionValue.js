var dataCustomActionValue1 = {
    id: 1
};

var dataCustomActionValue2 = {
    id: 2
};

var dataCustomActionValue3 = {
    id: 3
};

var mockCustomActionValue = function($q) {
    var model = mockModel($q, dataCustomActionValue1);

    return model;
};

angular.module('mock.customActionValue', []).service('CustomActionValue', mockCustomActionValue);

