var dataSubmissionRepo1 = [
    dataSubmission1,
    dataSubmission2,
    dataSubmission3
];

var dataSubmissionRepo2 = [
    dataSubmission3,
    dataSubmission2,
    dataSubmission1
];

var dataSubmissionRepo3 = [
    dataSubmission4,
    dataSubmission5,
    dataSubmission6
];

angular.module('mock.submissionRepo', []).service('SubmissionRepo', function($q) {
    var repo = mockRepo('SubmissionRepo', $q, mockSubmission, dataSubmissionRepo1);

    repo.batchAssignTo = function (assignee) {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    repo.batchExport = function (packager, filterId) {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    repo.batchPublish = function (depositLocation) {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    repo.batchUpdateStatus = function (submissionStatus) {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    repo.fetchSubmissionById = function (id) {
        var payload = repo.findById(id);

        if (payload === undefined) {
            // FIXME: callers, such as AdminSubmissionViewController, are not handling the reject case.
            // return a default payload as a work-around.
            payload = dataSubmissionRepo1[0];
            //return rejectPromise($q.defer());
        }

        return valuePromise($q.defer(), repo.mockModel(payload));
    };

    repo.query = function (columns, page, size) {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    return repo;
});
