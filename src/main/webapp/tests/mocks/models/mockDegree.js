var mockDegree1 = {
    'id': 1
};

var mockDegree2 = {
    'id': 2
};

var mockDegree3 = {
    'id': 3
};

var mockDegree = function($q) {
    var model = mockModel($q, mockDegree1);

    return model;
};

angular.module('mock.degree', []).service('Degree', mockDegree);

