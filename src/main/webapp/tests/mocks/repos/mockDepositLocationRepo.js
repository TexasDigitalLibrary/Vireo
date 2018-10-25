var mockDepositLocationRepo1 = [
    {
        "id": 1,
        "position": 1,
        "name": "Test0",
        "repository": "Dspace",
        "collection": null,
        "username": "test@tdl.org",
        "password": "abc123",
        "onBehalfOf": "TDL",
        "packager": "VireoExport",
        "depositor": "Sword1Deposit",
        "timeout": 100
    },
    {
        "id": 2,
        "position": 2,
        "name": "Test1",
        "repository": "Fedora",
        "collection": null,
        "username": "test@tdl.org",
        "password": "abc123",
        "onBehalfOf": "Texas A&M",
        "packager": "VireoExport",
        "depositor": "Sword1Deposit",
        "timeout": 200
    },
    {
        "id": 3,
        "position": 3,
        "name": "Test2",
        "repository": "Nuxio",
        "collection": null,
        "username": "test@tdl.org",
        "password": "abc123",
        "onBehalfOf": "Texas A&M",
        "packager": "VireoExport",
        "depositor": "FileDeposit",
        "timeout": 300
    }
];

var mockDepositLocationRepo2 = [
    {
        "id": 1,
        "position": 1,
        "name": "Test3",
        "repository": "Dspace",
        "collection": null,
        "username": "test@tdl.org",
        "password": "abc123",
        "onBehalfOf": "TDL",
        "packager": "VireoExport",
        "depositor": "Sword1Deposit",
        "timeout": 100
    },
    {
        "id": 2,
        "position": 2,
        "name": "Test4",
        "repository": "Fedora",
        "collection": null,
        "username": "test@tdl.org",
        "password": "abc123",
        "onBehalfOf": "Texas A&M",
        "packager": "VireoExport",
        "depositor": "Sword1Deposit",
        "timeout": 200
    },
    {
        "id": 3,
        "position": 3,
        "name": "Test5",
        "repository": "Nuxio",
        "collection": null,
        "username": "test@tdl.org",
        "password": "abc123",
        "onBehalfOf": "Texas A&M",
        "packager": "VireoExport",
        "depositor": "FileDeposit",
        "timeout": 300
    }
];

var mockDepositLocationRepo3 = [
    {
        "id": 1,
        "position": 1,
        "name": "Test3",
        "repository": "Dspace",
        "collection": null,
        "username": "test@tdl.org",
        "password": "abc123",
        "onBehalfOf": "TDL",
        "packager": "VireoExport",
        "depositor": "Sword1Deposit",
        "timeout": 100
    },
    {
        "id": 2,
        "position": 2,
        "name": "Test2",
        "repository": "Fedora",
        "collection": null,
        "username": "test@tdl.org",
        "password": "abc123",
        "onBehalfOf": "Texas A&M",
        "packager": "VireoExport",
        "depositor": "Sword1Deposit",
        "timeout": 200
    },
    {
        "id": 3,
        "position": 3,
        "name": "Test1",
        "repository": "Nuxio",
        "collection": null,
        "username": "test@tdl.org",
        "password": "abc123",
        "onBehalfOf": "Texas A&M",
        "packager": "VireoExport",
        "depositor": "FileDeposit",
        "timeout": 300
    }
];

angular.module('mock.depositLocationRepo', []).service('DepositLocationRepo', function ($q) {
    var repo = this;
    var defer;
    var validations = {};
    var validationResults = {};
    var originalList;

    var payloadResponse = function (payload) {
        return defer.resolve({
            body: angular.toJson({
                meta: {
                    status: 'SUCCESS'
                },
                payload: payload
            })
        });
    };

    var messageResponse = function (message) {
        return defer.resolve({
            body: angular.toJson({
                meta: {
                    status: 'SUCCESS',
                    message: message
                }
            })
        });
    };

    repo.list = [];

    repo.mock = function(toMock) {
        repo.list = toMock;
        this.originalList = toMock;
    };

    repo.mock(mockDepositLocationRepo1);

    repo.add = function (modelJson) {
        if (!repo.contains(modelJson)) {
            this.list.push(modelJson);
        }
    };

    repo.addAll = function (modelJsons) {
        for (var i in modelJsons) {
            repo.add(modelJsons[i]);
        }
    };

    repo.clearValidationResults = function () {
        validationResults = {};
    };

    repo.create = function (model) {
        defer = $q.defer();
        model.id = repo.list.length + 1;
        repo.list.push(angular.copy(model));
        payloadResponse(model);
        return defer.promise;
    };

    repo.contains = function (model) {
        var found = false;
        for (var i in repo.list) {
            if (repo.list[i].id === model.id) {
                found = true;
                break;
            }
        }
        return found;
    };

    repo.count = function () {
        return this.list.length;
    };

    repo.delete = function (model) {
        defer = $q.defer();
        for (var i in repo.list) {
            if (repo.list[i].id === model.id) {
                repo.list.splice(i, 1);
                break;
            }
        }
        payloadResponse();
        return defer.promise;
    };

    repo.deleteById = function (id) {
        defer = $q.defer();
        for (var i in repo.list) {
            if (repo.list[i].id === id) {
                repo.list.splice(i, 1);
                break;
            }
        }
        payloadResponse();
        return defer.promise;
    };

    repo.empty = function () {
        repo.list.length = 0;
    };

    repo.findById = function (id) {
        var found;
        for (var i in repo.list) {
            if (repo.list[i].id == id) {
                found = angular.copy(repo.list[i]);
            }
        }
        return found;
    };

    repo.getAll = function () {
        return angular.copy(repo.list);
    };

    repo.getAllFiltered = function(predicate) {
        var data = repo.list;
        var filteredData = [];

        // TODO

        return filteredData;
    };

    repo.getContents = function () {
        return angular.copy(repo.list);
    };

    repo.getEntityName = function () {
        return "DepositLocationRepo";
    };

    repo.getValidations = function () {
        return angular.copy(validations);
    };

    repo.getValidationResults = function () {
        return angular.copy(validationResults);
    };

    repo.listen = function (cbOrActionOrActionArray, cb) {
        // TODO
    };

    repo.ready = function () {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    repo.remove = function (modelToRemove) {
        for (var i in repo.list) {
            if (repo.list[i].id === modelToRemove.id) {
                repo.list.splice(i, 1);
                break;
            }
        }
    };

    repo.reset = function () {
        defer = $q.defer();
        repo.list = repo.originalList;
        payloadResponse();
        return defer.promise;
    };

    repo.save = function (model) {
        defer = $q.defer();
        // TODO
        payloadResponse({});
        return defer.promise;
    };

    repo.saveAll = function () {
        angular.forEach(repo.list, function (model) {
            repo.save(model);
        });
    };

    repo.setToDelete = function (id) {
        // TODO
    };

    repo.setToUpdate = function (id) {
        // TODO
    };

    repo.unshift = function (modelJson) {
        // TODO
    };

    repo.update = function (model) {
        defer = $q.defer();
        var updated;
        for (var i in repo.list) {
            if (repo.list[i].id === model.id) {
                updated = angular.copy(repo.list[i]);
                angular.extend(updated, model);
                break;
            }
        }
        payloadResponse(updated);
        return defer.promise;
    };

    return repo;
});
