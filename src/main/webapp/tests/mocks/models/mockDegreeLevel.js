var dataDegreeLevel1 = {
    id: 1
};

var dataDegreeLevel2 = {
    id: 2
};

var dataDegreeLevel3 = {
    id: 3
};

var mockDegreeLevel = function($q) {
    var model = mockModel($q, dataDegreeLevel1);

    return model;
};

angular.module('mock.degreeLevel', []).service('DegreeLevel', mockDegreeLevel);

