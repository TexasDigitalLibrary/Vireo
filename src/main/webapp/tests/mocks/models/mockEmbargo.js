var dataEmbargo1 = {
    id: 1
};

var dataEmbargo2 = {
    id: 2
};

var dataEmbargo3 = {
    id: 3
};

var mockEmbargo = function($q) {
    var model = mockModel($q, dataEmbargo1);

    return model;
};

angular.module('mock.embargo', []).service('Embargo', mockEmbargo);

