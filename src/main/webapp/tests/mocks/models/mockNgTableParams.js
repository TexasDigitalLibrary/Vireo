var dataNgTableParams1 = {
    id: 1,
    data: []
};

var dataNgTableParams2 = {
    id: 2,
    data: []
};

var dataNgTableParams3 = {
    id: 3,
    data: []
};

var dataNgTableParams4 = {
    id: 4,
    data: []
};

var dataNgTableParams5 = {
    id: 5,
    data: []
};

var dataNgTableParams6 = {
    id: 6,
    data: []
};

var mockNgTableParams = function($q) {
    var model = mockModel("NgTableParams", $q, dataNgTableParams1);

    model.count = function() {
        var total = 0;
        return total;
    };

    model.sorting = function(sort) {
        return {};
    };

    model.page = function(sort) {
        return {};
    };

    return model;
};

angular.module('mock.ngTableParams', []).service('NgTableParams', mockNgTableParams);

