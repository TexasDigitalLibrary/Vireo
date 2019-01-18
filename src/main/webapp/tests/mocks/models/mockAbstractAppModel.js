var dataAbstractAppModel1 = {
    id: 1
};

var dataAbstractAppModel2 = {
    id: 2
};

var mockAbstractAppModel3 = {
    id: 3
};

var dataAbstractAppModel4 = {
    id: 4
};

var dataAbstractAppModel5 = {
    id: 5
};

var mockAbstractAppModel6 = {
    id: 6
};

var mockAbstractAppModel = function($q) {
    var model = mockModel("AbstractAppModel", $q, dataAbstractAppModel1);

    return model;
};

angular.module('mock.abstractAppModel', []).service('AbstractAppModel', mockAbstractAppModel);
