var dataInputType1 = {
    id: 1
};

var dataInputType2 = {
    id: 2
};

var dataInputType3 = {
    id: 3
};

var dataInputType4 = {
    id: 4
};

var dataInputType5 = {
    id: 5
};

var dataInputType6 = {
    id: 6
};

var mockInputType = function($q) {
    var model = mockModel($q, dataInputType1);

    return model;
};

angular.module('mock.inputType', []).service('InputType', mockInputType);

