var mockSavedFilterRepo1 = [
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

var mockSavedFilterRepo2 = [
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

var mockSavedFilterRepo3 = [
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

angular.module('mock.savedFilterRepo', []).service('SavedFilterRepo', function($q) {
    var repo = mockRepo('SavedFilterRepo', $q, mockSavedFilter, mockSavedFilterRepo1);

    return repo;
});
