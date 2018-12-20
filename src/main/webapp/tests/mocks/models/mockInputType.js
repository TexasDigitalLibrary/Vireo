var mockInputType1 = {
    'id': 1
};

var mockInputType2 = {
    'id': 2
};

var mockInputType3 = {
    'id': 3
};

var mockInputType = function($q) {
    var model = mockModel($q, mockInputType1);

    return model;
};

angular.module('mock.inputType', []).service('InputType', mockInputType);

