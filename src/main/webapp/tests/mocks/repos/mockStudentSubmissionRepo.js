var mockStudentSubmissionRepo1 = [
    {
        id: 1,
        submissionStatus: {
            submissionState: "IN_PROGRESS"
        },
        submissionWorkflowSteps: [
        ],
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
        submissionWorkflowSteps: [
        ],
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
        submissionWorkflowSteps: [
        ],
        submitter: {
            uin: "123456789",
            lastName: "Daniels",
            firstName: "Jack",
            name: "jack",
            role: "ROLE_ADMIN"
        }
    }
];

var mockStudentSubmissionRepo2 = [
    {
        id: 1,
        submissionStatus: {
            submissionState: "IN_PROGRESS"
        },
        submissionWorkflowSteps: [
        ],
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
        submissionWorkflowSteps: [
        ],
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
        submissionWorkflowSteps: [
        ],
        submitter: {
            uin: "123456789",
            lastName: "Daniels",
            firstName: "Jack",
            name: "jack",
            role: "ROLE_ADMIN"
        }
    }
];

var mockStudentSubmissionRepo3 = [
    {
        id: 1,
        submissionStatus: {
            submissionState: "UNDER_REVIEW"
        },
        submissionWorkflowSteps: [
        ],
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
        submissionWorkflowSteps: [
        ],
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
        submissionWorkflowSteps: [
        ],
        submitter: {
            uin: "123456789",
            lastName: "Daniels",
            firstName: "Jack",
            name: "jack",
            role: "ROLE_ADMIN"
        }
    }
];

angular.module('mock.studentSubmissionRepo', []).service('StudentSubmissionRepo', function($q) {
    var repo = mockRepo('StudentSubmissionRepo', $q, mockStudentSubmission, mockStudentSubmissionRepo1);

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
