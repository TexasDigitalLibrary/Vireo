var mockSubmissionStatusRepo1 = [
    {
        id: 1,
        name: null
    },
    {
        id: 2,
        name: null
    },
    {
        id: 3,
        name: null
    }
];

var mockSubmissionStatusRepo2 = [
    {
        id: 1,
        name: null
    },
    {
        id: 2,
        name: null
    },
    {
        id: 3,
        name: null
    }
];

var mockSubmissionStatusRepo3 = [
    {
        id: 1,
        name: null
    },
    {
        id: 2,
        name: null
    },
    {
        id: 3,
        name: null
    }
];

angular.module('mock.submissionStatusRepo', []).service('SubmissionStatusRepo', function($q) {
    var repo = mockRepo('SubmissionStatusRepo', $q, mockSubmissionStatus, mockSubmissionStatusRepo1);

    repo.findById = function (id) {
        var found;
        for (var i in repo.list) {
            if (repo.list[i].id === id) {
                found = angular.copy(repo.list[i]);
            }
        }
        return found;
    };

    repo.findByName = function (name) {
        for (var i in repo.list) {
            if (repo.list[i].name === name) {
                return angular.copy(repo.list[i]);
            }
        }
    };

    return repo;
});
