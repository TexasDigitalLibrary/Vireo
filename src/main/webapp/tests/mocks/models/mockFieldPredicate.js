var dataFieldPredicate1 = {
    id: 1,
    documentTypePredicate: false,
    value: "Field Predicate 1"
};

var dataFieldPredicate2 = {
    id: 2,
    documentTypePredicate: false,
    value: "Field Predicate 2"
};

var dataFieldPredicate3 = {
    id: 3,
    documentTypePredicate: true,
    value: "Field Predicate 3"
};

var dataFieldPredicate4 = {
    id: 4,
    documentTypePredicate: true,
    value: "Field Predicate 4"
};

var dataFieldPredicate5 = {
    id: 5,
    documentTypePredicate: true,
    value: "Field Predicate 5"
};

var dataFieldPredicate6 = {
    id: 6,
    documentTypePredicate: true,
    value: "Field Predicate 6"
};

var mockFieldPredicate = function($q) {
    var model = mockModel($q, dataFieldPredicate1);

    return model;
};

angular.module('mock.fieldPredicate', []).service('FieldPredicate', mockFieldPredicate);

