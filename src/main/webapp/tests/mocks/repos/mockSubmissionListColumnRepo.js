var dataSubmissionListColumnRepo1 = [
    dataSubmissionListColumn1,
    dataSubmissionListColumn2,
    dataSubmissionListColumn3
];

var dataSubmissionListColumnRepo2 = [
    dataSubmissionListColumn3,
    dataSubmissionListColumn2,
    dataSubmissionListColumn1
];

var dataSubmissionListColumnRepo3 = [
    dataSubmissionListColumn4,
    dataSubmissionListColumn5,
    dataSubmissionListColumn6
];

angular.module("mock.submissionListColumnRepo", []).service("SubmissionListColumnRepo", function($q) {
    var repo = mockRepo("SubmissionListColumnRepo", $q, mockSubmissionListColumn, dataSubmissionListColumnRepo1);

    repo.findByTitle = function (title) {
        var found;
        for (var i in repo.mockedList) {
            if (repo.mockedList[i].title == title) {
                found = repo.mockCopy(repo.mockedList[i]);
            }
        }
        return found;
    };

    return repo;
});
