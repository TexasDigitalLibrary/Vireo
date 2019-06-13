var dataDegreeRepo1 = [
    dataDegree1,
    dataDegree2,
    dataDegree3
];

var dataDegreeRepo2 = [
    dataDegree3,
    dataDegree2,
    dataDegree1
];

var dataDegreeRepo3 = [
    dataDegree4,
    dataDegree5,
    dataDegree6
];

angular.module("mock.degreeRepo", []).service("DegreeRepo", function($q) {
    var repo = mockRepo("DegreeRepo", $q, mockDegree, dataDegreeRepo1);

    repo.getProquestDegreeCodes = function (cv) {
        var payload = {};
        return payloadPromise($q.defer(), payload);
    };

    return repo;
});
