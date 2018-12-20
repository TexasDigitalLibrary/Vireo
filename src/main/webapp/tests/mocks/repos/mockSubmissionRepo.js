var mockSubmissionRepo1 = [
    {
        "id": 1,
        "submitter": {
            "uin": "123456789",
            "lastName": "Daniels",
            "firstName": "Jack",
            "name": "jack",
            "role": "ROLE_ADMIN"
        },
        "fetchDocumentTypeFileInfo": function() {}
    },
    {
        "id": 2,
        "submitter": {
            "uin": "123456789",
            "lastName": "Daniels",
            "firstName": "Jack",
            "name": "jack",
            "role": "ROLE_ADMIN"
        },
        "fetchDocumentTypeFileInfo": function() {}
    },
    {
        "id": 3,
        "submitter": {
            "uin": "123456789",
            "lastName": "Daniels",
            "firstName": "Jack",
            "name": "jack",
            "role": "ROLE_ADMIN"
        },
        "fetchDocumentTypeFileInfo": function() {}
    }
];

var mockSubmissionRepo2 = [
    {
        "id": 1,
        "submitter": {
            "uin": "123456789",
            "lastName": "Daniels",
            "firstName": "Jack",
            "name": "jack",
            "role": "ROLE_ADMIN"
        },
        "fetchDocumentTypeFileInfo": function() {}
    },
    {
        "id": 2,
        "submitter": {
            "uin": "123456789",
            "lastName": "Daniels",
            "firstName": "Jack",
            "name": "jack",
            "role": "ROLE_ADMIN"
        },
        "fetchDocumentTypeFileInfo": function() {}
    },
    {
        "id": 3,
        "submitter": {
            "uin": "123456789",
            "lastName": "Daniels",
            "firstName": "Jack",
            "name": "jack",
            "role": "ROLE_ADMIN"
        },
        "fetchDocumentTypeFileInfo": function() {}
    }
];

var mockSubmissionRepo3 = [
    {
        "id": 1,
        "submitter": {
            "uin": "123456789",
            "lastName": "Daniels",
            "firstName": "Jack",
            "name": "jack",
            "role": "ROLE_ADMIN"
        },
        "fetchDocumentTypeFileInfo": function() {}
    },
    {
        "id": 2,
        "submitter": {
            "uin": "123456789",
            "lastName": "Daniels",
            "firstName": "Jack",
            "name": "jack",
            "role": "ROLE_ADMIN"
        },
        "fetchDocumentTypeFileInfo": function() {}
    },
    {
        "id": 3,
        "submitter": {
            "uin": "123456789",
            "lastName": "Daniels",
            "firstName": "Jack",
            "name": "jack",
            "role": "ROLE_ADMIN"
        },
        "fetchDocumentTypeFileInfo": function() {}
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
