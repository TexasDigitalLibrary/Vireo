var dataStudentSubmissionRepo1 = [
    dataStudentSubmission1,
    dataStudentSubmission2,
    dataStudentSubmission3
];

var dataStudentSubmissionRepo2 = [
    dataStudentSubmission3,
    dataStudentSubmission2,
    dataStudentSubmission1
];

var dataStudentSubmissionRepo3 = [
    dataStudentSubmission4,
    dataStudentSubmission5,
    dataStudentSubmission6
];

angular.module('mock.studentSubmissionRepo', []).service('StudentSubmissionRepo', function($q) {
    var repo = mockRepo('StudentSubmissionRepo', $q, mockStudentSubmission, dataStudentSubmissionRepo1);

    repo.create = function (model) {
        if (repo.mockedList === undefined) {
            repo.mockedList = [];
        }

        model.id = repo.mockedList.length + 1;
        repo.mockedList.push(repo.mockCopy(model));
        return payloadPromise($q.defer(), { Submission: model });
    };

    repo.fetchSubmissionById = function (id) {
        var payload = repo.findById(id);

        // TODO
        /*
        if (payload === undefined) {
            return rejectPromise($q.defer());
        }
        */

        return valuePromise($q.defer(), repo.mockModel(payload));
    };

    repo.listenForChanges = function () {
        return payloadPromise($q.defer());
    };

    return repo;
});
