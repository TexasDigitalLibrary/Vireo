var dataGraduationMonth1 = {
    id: 1
};

var dataGraduationMonth2 = {
    id: 2
};

var dataGraduationMonth3 = {
    id: 3
};

var mockGraduationMonth = function($q) {
    var model = mockModel($q, dataGraduationMonth1);

    return model;
};

angular.module('mock.graduationMonth', []).service('GraduationMonth', mockGraduationMonth);

