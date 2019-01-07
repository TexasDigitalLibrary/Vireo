var mockControlledVocabularyRepo1 = [
    {
        id: 1,
        position: 1,
        name: "guarantor",
        entityName: "Embargo",
        dictionary: [
            "DEFAULT",
            "PROQUEST"
        ],
        enum: true,
        entityProperty: true
    },
    {
        id: 2,
        position: 2,
        name: "type",
        entityName: "Attachment",
        dictionary: [],
        enum: false,
        entityProperty: true
    },
    {
        id: 3,
        position: 3,
        name: "test",
        entityName: null,
        dictionary: [],
        enum: false,
        entityProperty: false
    }
];

var mockControlledVocabularyRepo2 = [
    {
        id: 1,
        position: 1,
        name: "reviewer",
        entityName: null,
        dictionary: [],
        enum: true,
        entityProperty: true
    },
    {
        id: 2,
        position: 2,
        name: "type",
        entityName: "Attachment",
        dictionary: [],
        enum: false,
        entityProperty: true
    },
    {
        id: 3,
        position: 3,
        name: "test",
        entityName: null,
        dictionary: [],
        enum: false,
        entityProperty: false
    }
];

var mockControlledVocabularyRepo3 = [
    {
        id: 1,
        position: 1,
        name: "guarantor",
        entityName: "Embargo",
        dictionary: [
            "DEFAULT",
            "PROQUEST"
        ],
        enum: true,
        entityProperty: true
    },
    {
        id: 2,
        position: 2,
        name: "type",
        entityName: "Attachment",
        dictionary: [],
        enum: false,
        entityProperty: true
    },
    {
        id: 3,
        position: 3,
        name: "subjects",
        entityName: null,
        dictionary: [],
        enum: false,
        entityProperty: false
    }
];

angular.module('mock.controlledVocabularyRepo', []).service('ControlledVocabularyRepo', function($q) {
    var repo = mockRepo('ControlledVocabularyRepo', $q, mockControlledVocabulary, mockControlledVocabularyRepo1);

    repo.addVocabularyWord = function (cv, vw) {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    repo.cancel = function (controlledVocabulary) {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    repo.confirmCSV = function (controlledVocabulary) {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    repo.downloadCSV = function (controlledVocabulary) {
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    repo.removeVocabularyWord = function (cv, vw) {
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    repo.status = function (controlledVocabulary) {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    repo.updateVocabularyWord = function (cv, vw) {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    repo.uploadCSV = function (controlledVocabulary) {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    return repo;
});
