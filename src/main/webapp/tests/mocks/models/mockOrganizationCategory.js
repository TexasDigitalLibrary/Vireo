var dataOrganizationCategory1 = {
    id: 1
};

var dataOrganizationCategory2 = {
    id: 2
};

var dataOrganizationCategory3 = {
    id: 3
};

var dataOrganizationCategory4 = {
    id: 4
};

var dataOrganizationCategory5 = {
    id: 5
};

var dataOrganizationCategory6 = {
    id: 6
};

var mockOrganizationCategory = function($q) {
    var model = mockModel("OrganizationCategory", $q, dataOrganizationCategory1);

    return model;
};

angular.module('mock.organizationCategory', []).service('OrganizationCategory', mockOrganizationCategory);

