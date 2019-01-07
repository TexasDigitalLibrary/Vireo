var mockDegreeRepo1 = [
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

var mockDegreeRepo2 = [
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

var mockDegreeRepo3 = [
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

angular.module('mock.degreeRepo', []).service('DegreeRepo', function($q) {
    var repo = mockRepo('DegreeRepo', $q, mockDegree, mockDegreeRepo1);

    repo.getProquestDegreeCodes = function (cv) {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    return repo;
});
