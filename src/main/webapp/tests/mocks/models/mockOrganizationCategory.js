var dataOrganizationCategory1 = {
    id: 1
};

var dataOrganizationCategory2 = {
    id: 2
};

var dataOrganizationCategory3 = {
    id: 3
};

var mockOrganizationCategory = function($q) {
    var model = mockModel($q, dataOrganizationCategory1);

    return model;
};

angular.module('mock.organizationCategory', []).service('OrganizationCategory', mockOrganizationCategory);

