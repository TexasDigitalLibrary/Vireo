var dataFieldPredicate1 = {
    id: 1,
    documentTypePredicate: false,
    value: "_doctype_primary"
};

var dataFieldPredicate2 = {
    id: 2,
    documentTypePredicate: false,
    value: "_doctype_archived"
};

var dataFieldPredicate3 = {
    id: 3,
    documentTypePredicate: true,
    value: "text/plain"
};

var dataFieldPredicate4 = {
    id: 4,
    documentTypePredicate: true,
    value: "application/pdf"
};

var dataFieldPredicate5 = {
    id: 5,
    documentTypePredicate: true,
    value: "text/csv"
};

var dataFieldPredicate6 = {
    id: 6,
    documentTypePredicate: true,
    value: "image/png"
};

var mockFieldPredicate = function($q) {
    var model = mockModel($q, dataFieldPredicate1);

    return model;
};

angular.module('mock.fieldPredicate', []).service('FieldPredicate', mockFieldPredicate);

