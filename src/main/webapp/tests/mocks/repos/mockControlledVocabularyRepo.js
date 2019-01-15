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
        return payloadPromise($q.defer(), payload);
    };

    repo.cancel = function (controlledVocabulary) {
        var payload = {};
        return payloadPromise($q.defer(), payload);
    };

    repo.confirmCSV = function (file, name) {
        var payload = {
            HashMap: {
            }
        };

        return dataPromise($q.defer(), payload);
    };

    repo.downloadCSV = function (controlledVocabulary) {
        var payload = {
            HashMap: {
                headers: {},
                rows: 0
            }
        };

        return payloadPromise($q.defer(), payload);
    };

    repo.removeVocabularyWord = function (cv, vw) {
        var payload = {};
        return payloadPromise($q.defer(), payload);
    };

    repo.sort = function () {
        var payload = {};
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
        return payloadPromise($q.defer(), payload);
    };

    repo.uploadCSV = function (name) {
        var payload = {};
        return payloadPromise($q.defer(), payload);
    };

    return repo;
});
