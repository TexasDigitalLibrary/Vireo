var dataControlledVocabularyRepo1 = [
    dataControlledVocabulary1,
    dataControlledVocabulary2,
    dataControlledVocabulary3
];

var dataControlledVocabularyRepo2 = [
    dataControlledVocabulary3,
    dataControlledVocabulary2,
    dataControlledVocabulary1
];

var dataControlledVocabularyRepo3 = [
    dataControlledVocabulary4,
    dataControlledVocabulary5,
    dataControlledVocabulary6
];

angular.module('mock.controlledVocabularyRepo', []).service('ControlledVocabularyRepo', function($q) {
    var repo = mockRepo('ControlledVocabularyRepo', $q, mockControlledVocabulary, dataControlledVocabularyRepo1);

    repo.addVocabularyWord = function (cv, vw) {
        var payload = {};
        payload.VocabularyWord = vw;
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    repo.cancel = function (controlledVocabulary) {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    repo.confirmCSV = function (file, name) {
        var payload = {
            HashMap: {
            }
        };

        // TODO

        // ControlledVocabularyRepo.confirmCSV() return result appears to be inconsistent with the project.
        var response = {
            data: {
                meta: {
                    status: 'SUCCESS',
                },
                payload: payload,
                status: 200
            }
        };
        return valuePromise($q.defer(), response);
    };

    repo.downloadCSV = function (controlledVocabulary) {
        var payload = {
            HashMap: {
                headers: {},
                rows: 0
            }
        };

        // TODO
        return payloadPromise($q.defer(), payload);
    };

    repo.removeVocabularyWord = function (cv, vw) {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    repo.sort = function () {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    repo.status = function (controlledVocabulary) {
        var payload = {
            Boolean: false
        };
        return payloadPromise($q.defer(), payload);
    };

    repo.updateVocabularyWord = function (cv, vw) {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    repo.uploadCSV = function (name) {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    return repo;
});
