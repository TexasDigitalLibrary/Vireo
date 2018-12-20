var mockNgTableParams1 = {
};

var mockNgTableParams2 = {
};

var mockNgTableParams3 = {
};

var mockNgTableParams = function($q) {
    var model = mockModel($q, mockNgTableParams1);

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

