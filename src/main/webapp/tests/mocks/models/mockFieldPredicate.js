var mockFieldPredicate1 = {
    id: 1
};

var mockFieldPredicate2 = {
    id: 2
};

var mockFieldPredicate3 = {
    id: 3
};

var mockFieldPredicate = function($q) {
    var model = mockModel($q, mockFieldPredicate1);

    return model;
};

angular.module('mock.fieldPredicate', []).service('FieldPredicate', mockFieldPredicate);

