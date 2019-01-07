var mockManagerFilterColumnRepo1 = [
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

var mockManagerFilterColumnRepo2 = [
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

var mockManagerFilterColumnRepo3 = [
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

angular.module('mock.managerFilterColumnRepo', []).service('ManagerFilterColumnRepo', function($q) {
    var repo = mockRepo('ManagerFilterColumnRepo', $q);

    repo.updateFilterColumns = function (filterColumns) {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    return repo;
});
