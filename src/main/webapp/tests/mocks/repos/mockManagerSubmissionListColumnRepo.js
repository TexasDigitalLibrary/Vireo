var dataManagerSubmissionListColumnRepo1 = [
    dataSubmissionListColumn1,
    dataSubmissionListColumn2,
    dataSubmissionListColumn3
];

var dataManagerSubmissionListColumnRepo2 = [
    dataSubmissionListColumn3,
    dataSubmissionListColumn2,
    dataSubmissionListColumn1
];

var dataManagerSubmissionListColumnRepo3 = [
    dataSubmissionListColumn4,
    dataSubmissionListColumn5,
    dataSubmissionListColumn6
];

angular.module("mock.managerSubmissionListColumnRepo", []).service("ManagerSubmissionListColumnRepo", function($q) {
    var repo = mockRepo("ManagerSubmissionListColumnRepo", $q, mockSubmissionListColumn, dataManagerSubmissionListColumnRepo1);

    repo.resetSubmissionListColumns = function (cv) {
        var payload = {};
        return payloadPromise($q.defer(), payload);
    };

    repo.submissionListPageSize = function () {
        var payload = {};
        return payloadPromise($q.defer(), payload);
    };

    repo.updateSubmissionListColumns = function (columns, pageSize) {
        var payload = {};
        return payloadPromise($q.defer(), payload);
    };

    return repo;
});
