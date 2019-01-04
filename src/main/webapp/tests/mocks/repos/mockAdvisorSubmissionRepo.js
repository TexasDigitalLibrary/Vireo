var mockAdvisorSubmissionRepo1 = [
    {
        id: 1,
        submissionStatus: {
            submissionState: "IN_PROGRESS"
        },
        submitter: {
            uin: "123456789",
            lastName: "Daniels",
            firstName: "Jack",
            name: "jack",
            role: "ROLE_ADMIN"
        }
    },
    {
        id: 2,
        submissionStatus: {
            submissionState: "IN_PROGRESS"
        },
        submitter: {
            uin: "123456789",
            lastName: "Daniels",
            firstName: "Jack",
            name: "jack",
            role: "ROLE_ADMIN"
        }
    },
    {
        id: 3,
        submissionStatus: {
            submissionState: "IN_PROGRESS"
        },
        submitter: {
            uin: "123456789",
            lastName: "Daniels",
            firstName: "Jack",
            name: "jack",
            role: "ROLE_ADMIN"
        }
    }
];

var mockAdvisorSubmissionRepo2 = [
    {
        id: 1,
        submissionStatus: {
            submissionState: "IN_PROGRESS"
        },
        submitter: {
            uin: "123456789",
            lastName: "Daniels",
            firstName: "Jack",
            name: "jack",
            role: "ROLE_ADMIN"
        }
    },
    {
        id: 2,
        submissionStatus: {
            submissionState: "SUBMITTED"
        },
        submitter: {
            uin: "123456789",
            lastName: "Daniels",
            firstName: "Jack",
            name: "jack",
            role: "ROLE_ADMIN"
        }
    },
    {
        id: 3,
        submissionStatus: {
            submissionState: "SUBMITTED"
        },
        submitter: {
            uin: "123456789",
            lastName: "Daniels",
            firstName: "Jack",
            name: "jack",
            role: "ROLE_ADMIN"
        }
    }
];

var mockAdvisorSubmissionRepo3 = [
    {
        id: 1,
        submissionStatus: {
            submissionState: "UNDER_REVIEW"
        },
        submitter: {
            uin: "123456789",
            lastName: "Daniels",
            firstName: "Jack",
            name: "jack",
            role: "ROLE_ADMIN"
        }
    },
    {
        id: 2,
        submissionStatus: {
            submissionState: "ON_HOLD"
        },
        submitter: {
            uin: "123456789",
            lastName: "Daniels",
            firstName: "Jack",
            name: "jack",
            role: "ROLE_ADMIN"
        }
    },
    {
        id: 3,
        submissionStatus: {
            submissionState: "CANCELLED"
        },
        submitter: {
            uin: "123456789",
            lastName: "Daniels",
            firstName: "Jack",
            name: "jack",
            role: "ROLE_ADMIN"
        }
    }
];

angular.module('mock.advisorSubmissionRepo', []).service('AdvisorSubmissionRepo', function($q) {
    var repo = mockRepo('AdvisorSubmissionRepo', $q, mockSubmission, mockAdvisorSubmissionRepo1);

    repo.fetchDocumentTypeFileInfo = function() {
        // TODO
        return modelPromise($q.defer(), repo.mockModel(mockAdvisorSubmissionRepo1));
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

        return modelPromise($q.defer(), repo.mockModel(payload));
    };

    return repo;
});
