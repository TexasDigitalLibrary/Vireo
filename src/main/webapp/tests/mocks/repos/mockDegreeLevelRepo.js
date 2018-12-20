var mockDegreeLevelRepo1 = [
    {
        "id": 1
    },
    {
        "id": 2
    },
    {
        "id": 3
    }
];

var mockDegreeLevelRepo2 = [
    {
        "id": 1
    },
    {
        "id": 2
    },
    {
        "id": 3
    }
];

var mockDegreeLevelRepo3 = [
    {
        "id": 1
    },
    {
        "id": 2
    },
    {
        "id": 3
    }
];

angular.module('mock.degreeLevelRepo', []).service('DegreeLevelRepo', function($q) {
    var repo = mockRepo('DegreeLevelRepo', $q, mockDegreeLevel, mockDegreeLevelRepo1);

    return repo;
});
