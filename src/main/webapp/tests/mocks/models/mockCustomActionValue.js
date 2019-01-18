var dataCustomActionValue1 = {
    id: 1
};

var dataCustomActionValue2 = {
    id: 2
};

var dataCustomActionValue3 = {
    id: 3
};

var dataCustomActionValue4 = {
    id: 4
};

var dataCustomActionValue5 = {
    id: 5
};

var dataCustomActionValue6 = {
    id: 6
};

var mockCustomActionValue = function($q) {
    var model = mockModel("CustomActionValue", $q, dataCustomActionValue1);

    return model;
};

angular.module('mock.customActionValue', []).service('CustomActionValue', mockCustomActionValue);

