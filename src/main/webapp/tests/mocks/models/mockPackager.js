var dataPackager1 = {
    id: 1
};

var dataPackager2 = {
    id: 2
};

var dataPackager3 = {
    id: 3
};

var mockPackager = function($q) {
    var model = mockModel($q, dataPackager1);

    return model;
};

angular.module('mock.packager', []).service('Packager', mockPackager);

