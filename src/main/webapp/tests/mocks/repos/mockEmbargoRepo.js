var mockEmbargoRepo1 = [
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

var mockEmbargoRepo2 = [
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

var mockEmbargoRepo3 = [
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

angular.module('mock.embargoRepo', []).service('EmbargoRepo', function($q) {
    var repo = mockRepo('EmbargoRepo', $q, mockEmbargo, mockEmbargoRepo1);

    return repo;
});
