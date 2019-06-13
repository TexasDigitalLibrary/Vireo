var dataPackager1 = {
    id: 1
};

var dataPackager2 = {
    id: 2
};

var dataPackager3 = {
    id: 3
};

var dataPackager4 = {
    id: 4
};

var dataPackager5 = {
    id: 5
};

var dataPackager6 = {
    id: 6
};

var mockPackager = function($q) {
    var model = mockModel("Packager", $q, dataPackager1);

    return model;
};

angular.module("mock.packager", []).service("Packager", mockPackager);

