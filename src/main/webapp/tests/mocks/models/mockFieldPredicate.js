var mockFieldPredicate1 = {
    id: 1,
    documentTypePredicate: false,
    value: "Field Predicate 1"
};

var mockFieldPredicate2 = {
    id: 2,
    documentTypePredicate: false,
    value: "Field Predicate 2"
};

var mockFieldPredicate3 = {
    id: 3,
    documentTypePredicate: true,
    value: "Field Predicate 3"
};

var mockFieldPredicate = function($q) {
    var model = mockModel($q, mockFieldPredicate1);

    return model;
};

angular.module('mock.fieldPredicate', []).service('FieldPredicate', mockFieldPredicate);

