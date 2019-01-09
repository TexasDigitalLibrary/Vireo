var dataGraduationMonth1 = {
    id: 1,
    month: 1
};

var dataGraduationMonth2 = {
    id: 2,
    month: 2
};

var dataGraduationMonth3 = {
    id: 3,
    month: 3
};

var dataGraduationMonth4 = {
    id: 4,
    month: 4
};

var dataGraduationMonth5 = {
    id: 5,
    month: 5
};

var dataGraduationMonth6 = {
    id: 6,
    month: 6
};

var mockGraduationMonth = function($q) {
    var model = mockModel($q, dataGraduationMonth1);

    return model;
};

angular.module('mock.graduationMonth', []).service('GraduationMonth', mockGraduationMonth);

