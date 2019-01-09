var dataNgTableParams1 = {
    data: []
};

var dataNgTableParams2 = {
    data: []
};

var dataNgTableParams3 = {
    data: []
};

var mockNgTableParams = function($q) {
    var model = mockModel($q, dataNgTableParams1);

    model.count = function() {
        var total = 0;
        // TODO
        return total;
    };

    model.sorting = function(sort) {
        // TODO
        return {};
    };

    model.page = function(sort) {
        // TODO
        return {};
    };

    return model;
};

angular.module('mock.ngTableParams', []).service('NgTableParams', mockNgTableParams);

