var mockSubmissionListColumnRepo1 = [
    {
        id: 1,
        title: null
    },
    {
        id: 2,
        title: null
    },
    {
        id: 3,
        title: null
    }
];

var mockSubmissionListColumnRepo2 = [
    {
        id: 1,
        title: null
    },
    {
        id: 2,
        title: null
    },
    {
        id: 3,
        title: null
    }
];

var mockSubmissionListColumnRepo3 = [
    {
        id: 1,
        title: null
    },
    {
        id: 2,
        title: null
    },
    {
        id: 3,
        title: null
    }
];

angular.module('mock.submissionListColumnRepo', []).service('SubmissionListColumnRepo', function($q) {
    var repo = mockRepo('SubmissionListColumnRepo', $q, mockSubmissionListColumn, mockSubmissionListColumnRepo1);

    repo.findByTitle = function (title) {
        var found;
        for (var i in repo.mockedList) {
            if (repo.mockedList[i].title == title) {
                found = angular.copy(repo.mockedList[i]);
            }
        }
        return found;
    };


    return repo;
});
