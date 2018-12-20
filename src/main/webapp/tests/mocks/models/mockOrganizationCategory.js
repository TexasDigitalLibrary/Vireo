var mockOrganizationCategory1 = {
    'id': 1
};

var mockOrganizationCategory2 = {
    'id': 2
};

var mockOrganizationCategory3 = {
    'id': 3
};

var mockOrganizationCategory = function($q) {
    var model = mockModel($q, mockOrganizationCategory1);

    return model;
};

angular.module('mock.organizationCategory', []).service('OrganizationCategory', mockOrganizationCategory);

