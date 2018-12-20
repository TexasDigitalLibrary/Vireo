var mockDegreeLevel1 = {
    'id': 1
};

var mockDegreeLevel2 = {
    'id': 2
};

var mockDegreeLevel3 = {
    'id': 3
};

var mockDegreeLevel = function($q) {
    var model = mockModel($q, mockDegreeLevel1);

    return model;
};

angular.module('mock.degreeLevel', []).service('DegreeLevel', mockDegreeLevel);

