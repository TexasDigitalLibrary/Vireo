var dataFieldPredicateRepo1 = [
    dataFieldPredicate1,
    dataFieldPredicate2,
    dataFieldPredicate3
];

var dataFieldPredicateRepo2 = [
    dataFieldPredicate1,
    dataFieldPredicate2,
    dataFieldPredicate3
];

var dataFieldPredicateRepo3 = [
    dataFieldPredicate4,
    dataFieldPredicate5,
    dataFieldPredicate6
];

angular.module("mock.fieldPredicateRepo", []).service("FieldPredicateRepo", function($q) {
    var repo = mockRepo("FieldPredicateRepo", $q, mockFieldPredicate, dataFieldPredicateRepo1);

    repo.findByValue = function (value) {
        var payload = {};
        return payloadPromise($q.defer(), payload);
    };

    return repo;
});
