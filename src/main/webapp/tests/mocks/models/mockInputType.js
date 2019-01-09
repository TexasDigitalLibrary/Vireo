var dataInputType1 = {
    id: 1
};

var dataInputType2 = {
    id: 2
};

var dataInputType3 = {
    id: 3
};

var mockInputType = function($q) {
    var model = mockModel($q, dataInputType1);

    return model;
};

angular.module('mock.inputType', []).service('InputType', mockInputType);

