var mockInputTypeRepo1 = [
    {
        id: 1
    },
    {
        id: 2
    },
    {
        id: 3
    }
];

var mockInputTypeRepo2 = [
    {
        id: 1
    },
    {
        id: 2
    },
    {
        id: 3
    }
];

var mockInputTypeRepo3 = [
    {
        id: 1
    },
    {
        id: 2
    },
    {
        id: 3
    }
];

angular.module('mock.inputTypeRepo', []).service('InputTypeRepo', function($q) {
    var repo = mockRepo('InputTypeRepo', $q, mockInputType, mockInputTypeRepo1);

    return repo;
});
