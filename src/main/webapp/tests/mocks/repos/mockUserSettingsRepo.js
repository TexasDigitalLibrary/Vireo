var mockUserSettingsRepo1 = [
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

var mockUserSettingsRepo2 = [
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

var mockUserSettingsRepo3 = [
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

angular.module('mock.userSettingsRepo', []).service('UserSettingsRepo', function($q) {
    var repo = mockRepo('UserSettingsRepo', $q, mockUserSettings, mockUserSettingsRepo1);

    return repo;
});
