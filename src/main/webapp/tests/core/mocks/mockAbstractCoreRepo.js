var mockRepo = function (RepoName, $q, mockModelCtor, mockDataArray) {
    var repo = {};

    var validations = {};

    var validationResults = {};

    var originalList;

    repo.mockedList = [];

    repo.mock = function(toMock) {
        repo.mockedList = [];
        originalList = [];

        if (typeof mockModelCtor === "function" && typeof toMock === "object") {
            for (var i in toMock) {
                var model = repo.mockModel(toMock[i]);
                repo.mockedList.push(model);
                originalList.push(model);
            }
        }
    };

    repo.mockModel = function(toMock) {
        if (typeof mockModelCtor === "function") {
            var mocked = new mockModelCtor($q);
            mocked.mock(toMock);
            return mocked;
        }

        return toMock;
    };

    repo.mock(mockDataArray);

    repo.add = function (modelJson) {
        if (!repo.contains(modelJson)) {
            repo.mockedList.push(modelJson);
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
        model.id = repo.mockedList.length + 1;
        repo.mockedList.push(angular.copy(model));
        return payloadPromise($q.defer(), model);
    };

    repo.contains = function (model) {
        var found = false;
        for (var i in repo.mockedList) {
            if (repo.mockedList[i].id === model.id) {
                found = true;
                break;
            }
        }
        return found;
    };

    repo.count = function () {
        return repo.mockedList.length;
    };

    repo.delete = function (model) {
        for (var i in repo.mockedList) {
            if (repo.mockedList[i].id === model.id) {
                repo.mockedList.splice(i, 1);
                break;
            }
        }
        return payloadPromise($q.defer());
    };

    repo.deleteById = function (id) {
        for (var i in repo.mockedList) {
            if (repo.mockedList[i].id === id) {
                repo.mockedList.splice(i, 1);
                break;
            }
        }
        return payloadPromise($q.defer());
    };

    repo.empty = function () {
        repo.list.length = 0;
    };

    repo.fetch = function () {
        return payloadPromise($q.defer(), mockDataArray);
    };

    repo.findById = function (id) {
        var found;
        for (var i in repo.mockedList) {
            if (repo.mockedList[i].id == id) {
                found = angular.copy(repo.mockedList[i]);
            }
        }
        return found;
    };

    repo.getAll = function () {
        return angular.copy(repo.mockedList);
    };

    repo.getAllFiltered = function(predicate) {
        var filteredData = [];

        angular.forEach(repo.list, function(datum) {
            if (predicate(datum)) {
                filteredData.push(datum);
            }
        });

        return filteredData;
    };

    repo.getContents = function () {
        return angular.copy(repo.mockedList);
    };

    repo.getEntityName = function () {
        return RepoName;
    };

    repo.getValidations = function () {
        return angular.copy(validations);
    };

    repo.getValidationResults = function () {
        return angular.copy(validationResults);
    };

    repo.listen = function (cbOrActionOrActionArray, cb) {
        if (typeof cbOrActionOrActionArray === "function") {
            var apiRes = {
                meta: {
                    status: 'SUCCESS',
                    message: ""
                },
                status: 200
            };
            cbOrActionOrActionArray(apiRes);
        }
        else if (Array.isArray(cbOrActionOrActionArray)) {
            for (var cbAction in cbOrActionOrActionArray) {
                if (typeof cbAction === "function") {
                    cbAction();
                }
            }
        }
        else if (typeof cb === "function") {
            cb();
        }
        return payloadPromise($q.defer());
    };

    repo.ready = function () {
        return payloadPromise($q.defer(), mockDataArray);
    };

    repo.remove = function (modelToRemove) {
        if (typeof modelToRemove === "object") {
            for (var i in repo.mockedList) {
                if (repo.mockedList[i].id === modelToRemove.id) {
                    repo.mockedList.splice(i, 1);
                    break;
                }
            }
        }
    };

    repo.reset = function () {
        repo.mockedList = repo.originalList;
        return payloadPromise($q.defer());
    };

    repo.save = function (modelToSave) {
        if (typeof modelToSave === "object") {
            var isNew = true;
            var savedModel = repo.mockModel(modelToSave);
            for (var i in repo.mockedList) {
                if (repo.mockedList[i].id === modelToSave.id) {
                    angular.extend(repo.mockedList[i], savedModel);
                    isNew = false;
                    break;
                }
            }

            if (isNew) {
                repo.mockedList[repo.mockedList.length] = savedModel;
            }

            return payloadPromise($q.defer(), savedModel);
        }

        return rejectPromise($q.defer());
    };

    repo.saveAll = function () {
        angular.forEach(repo.mockedList, function (model) {
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
        var updated;
        for (var i in repo.mockedList) {
            if (repo.mockedList[i].id === model.id) {
                updated = angular.copy(repo.mockedList[i]);
                angular.extend(updated, model);
                break;
            }
        }
        return payloadPromise($q.defer(), updated);
    };

    return repo;
};
