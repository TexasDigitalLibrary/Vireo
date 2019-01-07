var mockPackager1 = {
    id: 1
};

var mockPackager2 = {
    id: 2
};

var mockPackager3 = {
    id: 3
};

var mockPackager = function($q) {
    var model = mockModel($q, mockPackager1);

    return model;
};

angular.module('mock.packager', []).service('Packager', mockPackager);

