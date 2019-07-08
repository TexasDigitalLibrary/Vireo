var dataSubmissionStatusRepo1 = [
    dataSubmissionStatus1,
    dataSubmissionStatus2,
    dataSubmissionStatus3
];

var dataSubmissionStatusRepo2 = [
    dataSubmissionStatus3,
    dataSubmissionStatus2,
    dataSubmissionStatus1
];

var dataSubmissionStatusRepo3 = [
    dataSubmissionStatus4,
    dataSubmissionStatus5,
    dataSubmissionStatus6
];

angular.module("mock.submissionStatusRepo", []).service("SubmissionStatusRepo", function($q) {
    var repo = mockRepo("SubmissionStatusRepo", $q, mockSubmissionStatus, dataSubmissionStatusRepo1);

    repo.findById = function (id) {
        var found;
        for (var i in repo.list) {
            if (repo.list[i].id === id) {
                found = repo.mockCopy(repo.list[i]);
            }
        }
        return found;
    };

    repo.findByName = function (name) {
        for (var i in repo.list) {
            if (repo.list[i].name === name) {
                return repo.mockCopy(repo.list[i]);
            }
        }
    };

    return repo;
});
