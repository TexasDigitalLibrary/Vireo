var mockGraduationMonth1 = {
    id: 1
};

var mockGraduationMonth2 = {
    id: 2
};

var mockGraduationMonth3 = {
    id: 3
};

var mockGraduationMonth = function($q) {
    var model = mockModel($q, mockGraduationMonth1);

    return model;
};

angular.module('mock.graduationMonth', []).service('GraduationMonth', mockGraduationMonth);

