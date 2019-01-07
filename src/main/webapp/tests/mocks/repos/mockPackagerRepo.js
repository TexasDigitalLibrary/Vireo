var mockPackagerRepo1 = [
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

var mockPackagerRepo2 = [
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

var mockPackagerRepo3 = [
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

angular.module('mock.packagerRepo', []).service('PackagerRepo', function($q) {
    var repo = mockRepo('PackagerRepo', $q, mockPackager, mockPackagerRepo1);

    return repo;
});
