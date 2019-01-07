var mockSubmissionRepo1 = [
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

var mockSubmissionRepo2 = [
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

var mockSubmissionRepo3 = [
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

angular.module('mock.submissionRepo', []).service('SubmissionRepo', function($q) {
    var repo = mockRepo('SubmissionRepo', $q, mockSubmission, mockSubmissionRepo1);

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
            return rejectPromise($q.defer());
        }

        return modelPromise($q.defer(), repo.mockModel(payload));
    };

    repo.query = function (columns, page, size) {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    return repo;
});
