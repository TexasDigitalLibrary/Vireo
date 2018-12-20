var mockFieldPredicateRepo1 = [
    {
        "id": 1
    },
    {
        "id": 2
    },
    {
        "id": 3
    }
];

var mockFieldPredicateRepo2 = [
    {
        "id": 1
    },
    {
        "id": 2
    },
    {
        "id": 3
    }
];

var mockFieldPredicateRepo3 = [
    {
        "id": 1
    },
    {
        "id": 2
    },
    {
        "id": 3
    }
];

angular.module('mock.fieldPredicateRepo', []).service('FieldPredicateRepo', function($q) {
    var repo = mockRepo('FieldPredicateRepo', $q, mockFieldPredicate, mockFieldPredicateRepo1);

    repo.findByValue = function (value) {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    return repo;
});
