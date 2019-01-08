var mockControlledVocabularyRepo1 = [
    {
        id: 1,
        position: 1,
        name: "guarantor",
        entityName: "Embargo",
        dictionary: [
            {
                id: 1,
                adding: false,
                beginAdd: false,
                clickedCell: false,
                contacts: "a,b",
                definition: "",
                editing: false,
                identifier: "vw1",
                moving: false,
                name: "DEFAULT"
            },
            {
                id: 2,
                adding: false,
                beginAdd: false,
                clickedCell: false,
                contacts: ["a", "c"],
                definition: "",
                editing: false,
                identifier: "vw2",
                moving: false,
                name: "PROQUEST"
            },
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
            {
                id: 1,
                adding: false,
                beginAdd: false,
                clickedCell: false,
                contacts: "a,b",
                definition: "",
                editing: false,
                identifier: "vw1",
                moving: false,
                name: "DEFAULT"
            },
            {
                id: 2,
                adding: false,
                beginAdd: false,
                clickedCell: false,
                contacts: ["a", "c"],
                definition: "",
                editing: false,
                identifier: "vw2",
                moving: false,
                name: "PROQUEST"
            },
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

    repo.reorder = function (src, dest) {
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
