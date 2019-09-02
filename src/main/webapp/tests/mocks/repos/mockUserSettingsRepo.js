var dataUserSettingsRepo1 = [
    dataUserSettings1,
    dataUserSettings2,
    dataUserSettings3
];

var dataUserSettingsRepo2 = [
    dataUserSettings3,
    dataUserSettings2,
    dataUserSettings1
];

var dataUserSettingsRepo3 = [
    dataUserSettings4,
    dataUserSettings5,
    dataUserSettings6
];

angular.module("mock.userSettingsRepo", []).service("UserSettingsRepo", function($q) {
    var repo = mockRepo("UserSettingsRepo", $q, mockUserSettings, dataUserSettingsRepo1);

    return repo;
});
