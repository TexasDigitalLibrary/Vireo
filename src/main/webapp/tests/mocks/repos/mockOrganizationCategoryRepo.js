var mockOrganizationCategoryRepo1 = [
    {
        "id": 1
    },
    {
        "id": 2
    },
    {
        "id": 3
    }
];

var mockOrganizationCategoryRepo2 = [
    {
        "id": 1
    },
    {
        "id": 2
    },
    {
        "id": 3
    }
];

var mockOrganizationCategoryRepo3 = [
    {
        "id": 1
    },
    {
        "id": 2
    },
    {
        "id": 3
    }
];

angular.module('mock.organizationCategoryRepo', []).service('OrganizationCategoryRepo', function($q) {
    var repo = mockRepo('OrganizationCategoryRepo', $q, mockOrganizationCategory, mockOrganizationCategoryRepo1);

    return repo;
});
