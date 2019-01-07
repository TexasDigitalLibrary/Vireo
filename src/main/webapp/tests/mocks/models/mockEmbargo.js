var mockEmbargo1 = {
    id: 1
};

var mockEmbargo2 = {
    id: 2
};

var mockEmbargo3 = {
    id: 3
};

var mockEmbargo = function($q) {
    var model = mockModel($q, mockEmbargo1);

    return model;
};

angular.module('mock.embargo', []).service('Embargo', mockEmbargo);

