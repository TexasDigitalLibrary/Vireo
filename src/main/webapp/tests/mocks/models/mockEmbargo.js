var dataEmbargo1 = {
    id: 1
};

var dataEmbargo2 = {
    id: 2
};

var dataEmbargo3 = {
    id: 3
};

var dataEmbargo4 = {
    id: 4
};

var dataEmbargo5 = {
    id: 5
};

var dataEmbargo6 = {
    id: 6
};

var mockEmbargo = function($q) {
    var model = mockModel($q, dataEmbargo1);

    return model;
};

angular.module('mock.embargo', []).service('Embargo', mockEmbargo);

