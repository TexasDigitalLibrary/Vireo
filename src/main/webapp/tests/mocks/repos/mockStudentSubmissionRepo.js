var mockStudentSubmissionRepo1 = [
    {
        "id": 1,
        "submitter": {
            "uin": "123456789",
            "lastName": "Daniels",
            "firstName": "Jack",
            "name": "jack",
            "role": "ROLE_ADMIN"
        }
    },
    {
        "id": 2,
        "submitter": {
            "uin": "123456789",
            "lastName": "Daniels",
            "firstName": "Jack",
            "name": "jack",
            "role": "ROLE_ADMIN"
        }
    },
    {
        "id": 3,
        "submitter": {
            "uin": "123456789",
            "lastName": "Daniels",
            "firstName": "Jack",
            "name": "jack",
            "role": "ROLE_ADMIN"
        }
    }
];

var mockStudentSubmissionRepo2 = [
    {
        "id": 1,
        "submitter": {
            "uin": "123456789",
            "lastName": "Daniels",
            "firstName": "Jack",
            "name": "jack",
            "role": "ROLE_ADMIN"
        }
    },
    {
        "id": 2,
        "submitter": {
            "uin": "123456789",
            "lastName": "Daniels",
            "firstName": "Jack",
            "name": "jack",
            "role": "ROLE_ADMIN"
        }
    },
    {
        "id": 3,
        "submitter": {
            "uin": "123456789",
            "lastName": "Daniels",
            "firstName": "Jack",
            "name": "jack",
            "role": "ROLE_ADMIN"
        }
    }
];

var mockStudentSubmissionRepo3 = [
    {
        "id": 1,
        "submitter": {
            "uin": "123456789",
            "lastName": "Daniels",
            "firstName": "Jack",
            "name": "jack",
            "role": "ROLE_ADMIN"
        }
    },
    {
        "id": 2,
        "submitter": {
            "uin": "123456789",
            "lastName": "Daniels",
            "firstName": "Jack",
            "name": "jack",
            "role": "ROLE_ADMIN"
        }
    },
    {
        "id": 3,
        "submitter": {
            "uin": "123456789",
            "lastName": "Daniels",
            "firstName": "Jack",
            "name": "jack",
            "role": "ROLE_ADMIN"
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

        return modelPromise($q.defer(), repo.mockModel(payload));
    };

    repo.listenForChanges = function () {
        return payloadPromise($q.defer());
    };

    return repo;
});
