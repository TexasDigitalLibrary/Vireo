var mockAbstractAppModel1 = {
    'id': 1
};

var mockAbstractAppModel2 = {
    'id': 2
};

var mockAbstractAppModel3 = {
    'id': 3
};

var mockAbstractAppModel = function($q) {
    var model = mockModel($q, mockAbstractAppModel1);

    return model;
};

angular.module('mock.abstractAppModel', []).service('AbstractAppModel', mockAbstractAppModel);
