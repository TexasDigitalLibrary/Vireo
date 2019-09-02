var dataOrganizationCategoryRepo1 = [
    dataOrganizationCategory1,
    dataOrganizationCategory2,
    dataOrganizationCategory3
];

var dataOrganizationCategoryRepo2 = [
    dataOrganizationCategory3,
    dataOrganizationCategory2,
    dataOrganizationCategory1
];

var dataOrganizationCategoryRepo3 = [
    dataOrganizationCategory4,
    dataOrganizationCategory5,
    dataOrganizationCategory6
];

angular.module("mock.organizationCategoryRepo", []).service("OrganizationCategoryRepo", function($q) {
    var repo = mockRepo("OrganizationCategoryRepo", $q, mockOrganizationCategory, dataOrganizationCategoryRepo1);

    return repo;
});
