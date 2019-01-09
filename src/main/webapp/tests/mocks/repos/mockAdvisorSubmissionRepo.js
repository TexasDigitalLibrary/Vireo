var dataAdvisorSubmissionRepo1 = [
    dataSubmission1,
    dataSubmission2,
    dataSubmission3
];

var dataAdvisorSubmissionRepo2 = [
    dataSubmission3,
    dataSubmission2,
    dataSubmission1
];

var dataAdvisorSubmissionRepo3 = [
    dataSubmission4,
    dataSubmission5,
    dataSubmission6
];

angular.module('mock.advisorSubmissionRepo', []).service('AdvisorSubmissionRepo', function($q) {
    var repo = mockRepo('AdvisorSubmissionRepo', $q, mockSubmission, dataAdvisorSubmissionRepo1);

    repo.fetchDocumentTypeFileInfo = function() {
        // TODO
        return valuePromise($q.defer(), repo.mockModel(dataAdvisorSubmissionRepo1));
    };

    repo.fetchSubmissionByHash = function (hash) {
        var payload;

        for (var i in repo.list) {
            if (repo.list[i].hash === hash) {
                payload = angular.copy(repo.list[i]);
                break;
            }
        }

        // TODO
        /*
        if (payload === undefined) {
            return rejectPromise($q.defer());
        }
        */

        return valuePromise($q.defer(), repo.mockModel(payload));
    };

    return repo;
});
