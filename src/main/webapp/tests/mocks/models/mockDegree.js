var dataDegree1 = {
    id: 1
};

var dataDegree2 = {
    id: 2
};

var dataDegree3 = {
    id: 3
};

var dataDegree4 = {
    id: 4
};

var dataDegree5 = {
    id: 5
};

var dataDegree6 = {
    id: 6
};

var mockDegree = function($q) {
    var model = mockModel($q, dataDegree1);

    return model;
};

angular.module('mock.degree', []).service('Degree', mockDegree);

