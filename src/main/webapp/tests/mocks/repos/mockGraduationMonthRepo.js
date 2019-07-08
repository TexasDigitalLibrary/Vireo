var dataGraduationMonthRepo1 = [
    dataGraduationMonth1,
    dataGraduationMonth2,
    dataGraduationMonth3
];

var dataGraduationMonthRepo2 = [
    dataGraduationMonth3,
    dataGraduationMonth2,
    dataGraduationMonth1
];

var dataGraduationMonthRepo3 = [
    dataGraduationMonth4,
    dataGraduationMonth5,
    dataGraduationMonth6
];

angular.module("mock.graduationMonthRepo", []).service("GraduationMonthRepo", function($q) {
    var repo = mockRepo("GraduationMonthRepo", $q, mockGraduationMonth, dataGraduationMonthRepo1);

    return repo;
});
