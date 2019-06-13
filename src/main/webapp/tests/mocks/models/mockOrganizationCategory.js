var dataOrganizationCategory1 = {
    id: 1,
    name: "OrganizationCategory1"
};

var dataOrganizationCategory2 = {
    id: 2,
    name: "OrganizationCategory2"
};

var dataOrganizationCategory3 = {
    id: 3,
    name: "OrganizationCategory3"
};

var dataOrganizationCategory4 = {
    id: 4,
    name: "OrganizationCategory4"
};

var dataOrganizationCategory5 = {
    id: 5,
    name: "OrganizationCategory5"
};

var dataOrganizationCategory6 = {
    id: 6,
    name: "OrganizationCategory6"
};

var mockOrganizationCategory = function($q) {
    var model = mockModel("OrganizationCategory", $q, dataOrganizationCategory1);

    return model;
};

angular.module("mock.organizationCategory", []).service("OrganizationCategory", mockOrganizationCategory);

