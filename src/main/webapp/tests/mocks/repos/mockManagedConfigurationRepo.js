var dataManagedConfigurationRepo1 = [
    dataManagedConfiguration1,
    dataManagedConfiguration2,
    dataManagedConfiguration3
];

var dataManagedConfigurationRepo2 = [
    dataManagedConfiguration3,
    dataManagedConfiguration2,
    dataManagedConfiguration1
];

var dataManagedConfigurationRepo3 = [
    dataManagedConfiguration4,
    dataManagedConfiguration5,
    dataManagedConfiguration6
];

angular.module("mock.managedConfigurationRepo", []).service("ManagedConfigurationRepo", function($q) {
    var repo = mockRepo("ManagedConfigurationRepo", $q, mockManagedConfiguration, dataManagedConfigurationRepo1);

    repo.build = function (data) {
        var payload = {};
        return payloadPromise($q.defer(), payload);
    };

    repo.findByTypeAndName = function (type, name) {
        var found;
        for (var i in repo.mockedList) {
            if (repo.mockedList[i].type == type && repo.mockedList[i].name == name) {
                found = repo.mockCopy(repo.mockedList[i]);
            }
        }
        return found;
    };

    repo.getAllShibbolethConfigurations = function () {
        var payload = {};
        return payloadPromise($q.defer(), payload);
    };

    repo.unwrap = function (res) {
        var payload = {};
        return payloadPromise($q.defer(), payload);
    };

    return repo;
});
