var mockFieldPredicateRepo1 = [
    {
        id: 1,
        documentTypePredicate: false,
        value: "Field Predicate 1"
    },
    {
        id: 2,
        documentTypePredicate: false,
        value: "Field Predicate 2"
    },
    {
        id: 3,
        documentTypePredicate: true,
        value: "Field Predicate 3"
    }
];

var mockFieldPredicateRepo2 = [
    {
        id: 4,
        documentTypePredicate: true,
        value: "Field Predicate 4"
    },
    {
        id: 5,
        documentTypePredicate: false,
        value: "Field Predicate 5"
    },
    {
        id: 6,
        documentTypePredicate: false,
        value: "Field Predicate 6"
    }
];

var mockFieldPredicateRepo3 = [
    {
        id: 3,
        documentTypePredicate: true,
        value: "Field Predicate 3"
    },
    {
        id: 4,
        documentTypePredicate: true,
        value: "Field Predicate 4"
    },
    {
        id: 7,
        documentTypePredicate: true,
        value: "Field Predicate 7"
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
