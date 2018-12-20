var mockManagedConfigurationRepo1 = [
    {
        "id": 1,
        "name": null,
        "type": null
    },
    {
        "id": 2,
        "name": null,
        "type": null
    },
    {
        "id": 3,
        "name": null,
        "type": null
    }
];

var mockManagedConfigurationRepo2 = [
    {
        "id": 1,
        "name": null,
        "type": null
    },
    {
        "id": 2,
        "name": null,
        "type": null
    },
    {
        "id": 3,
        "name": null,
        "type": null
    }
];

var mockManagedConfigurationRepo3 = [
    {
        "id": 1,
        "name": null,
        "type": null
    },
    {
        "id": 2,
        "name": null,
        "type": null
    },
    {
        "id": 3,
        "name": null,
        "type": null
    }
];

angular.module('mock.managedConfigurationRepo', []).service('ManagedConfigurationRepo', function($q) {
    var repo = mockRepo('ManagedConfigurationRepo', $q, mockManagedConfiguration, mockManagedConfigurationRepo1);

    repo.build = function (data) {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    repo.findByTypeAndName = function (type, name) {
        var found;
        for (var i in repo.mockedList) {
            if (repo.mockedList[i].type == type && repo.mockedList[i].name == name) {
                found = angular.copy(repo.mockedList[i]);
            }
        }
        return found;
    };

    repo.getAllShibbolethConfigurations = function () {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    repo.unwrap = function (res) {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    return repo;
});
