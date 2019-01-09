angular.module('mock.managerFilterColumnRepo', []).service('ManagerFilterColumnRepo', function($q) {
    var repo = mockRepo('ManagerFilterColumnRepo', $q);

    repo.updateFilterColumns = function (filterColumns) {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    return repo;
});
