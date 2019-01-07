var mockManagerSubmissionListColumnRepo1 = [
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

var mockManagerSubmissionListColumnRepo2 = [
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

var mockManagerSubmissionListColumnRepo3 = [
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

angular.module('mock.managerSubmissionListColumnRepo', []).service('ManagerSubmissionListColumnRepo', function($q) {
    var repo = mockRepo('ManagerSubmissionListColumnRepo', $q, null, mockManagerSubmissionListColumnRepo1);

    repo.resetSubmissionListColumns = function (cv) {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    repo.submissionListPageSize = function () {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    repo.updateSubmissionListColumns = function (columns, pageSize) {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    return repo;
});
