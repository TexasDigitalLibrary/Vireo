var mockFieldProfileRepo1 = [
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

var mockFieldProfileRepo2 = [
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

var mockFieldProfileRepo3 = [
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

angular.module('mock.fieldProfileRepo', []).service('FieldProfileRepo', function($q) {
    var repo = mockRepo('FieldProfileRepo', $q, mockFieldProfile, mockFieldProfileRepo1);

    return repo;
});
