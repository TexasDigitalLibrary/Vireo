var mockGraduationMonthRepo1 = [
    {
        id: 1
    },
    {
        id: 2
    },
    {
        id: 3
    }
];

var mockGraduationMonthRepo2 = [
    {
        id: 1
    },
    {
        id: 2
    },
    {
        id: 3
    }
];

var mockGraduationMonthRepo3 = [
    {
        id: 1
    },
    {
        id: 2
    },
    {
        id: 3
    }
];

angular.module('mock.graduationMonthRepo', []).service('GraduationMonthRepo', function($q) {
    var repo = mockRepo('GraduationMonthRepo', $q, mockGraduationMonth, mockGraduationMonthRepo1);

    return repo;
});
