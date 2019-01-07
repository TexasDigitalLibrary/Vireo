var mockCustomActionValueRepo1 = [
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

var mockCustomActionValueRepo2 = [
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

var mockCustomActionValueRepo3 = [
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

angular.module('mock.customActionValueRepo', []).service('CustomActionValueRepo', function($q) {
    var repo = mockRepo('CustomActionValueRepo', $q, mockCustomActionValue, mockCustomActionValueRepo1);

    return repo;
});
