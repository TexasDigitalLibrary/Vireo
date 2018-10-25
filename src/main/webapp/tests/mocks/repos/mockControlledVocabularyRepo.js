var mockControlledVocabularyRepo1 = [
    {
        "id": 1,
        "position": 1,
        "name": "guarantor",
        "entityName": "Embargo",
        "dictionary": [
            "DEFAULT",
            "PROQUEST"
        ],
        "enum": true,
        "entityProperty": true
    },
    {
        "id": 2,
        "position": 2,
        "name": "type",
        "entityName": "Attachment",
        "dictionary": [],
        "enum": false,
        "entityProperty": true
    },
    {
        "id": 3,
        "position": 3,
        "name": "test",
        "entityName": null,
        "dictionary": [],
        "enum": false,
        "entityProperty": false
    }
];

var mockControlledVocabularyRepo2 = [
    {
        "id": 1,
        "position": 1,
        "name": "reviewer",
        "entityName": null,
        "dictionary": [],
        "enum": true,
        "entityProperty": true
    },
    {
        "id": 2,
        "position": 2,
        "name": "type",
        "entityName": "Attachment",
        "dictionary": [],
        "enum": false,
        "entityProperty": true
    },
    {
        "id": 3,
        "position": 3,
        "name": "test",
        "entityName": null,
        "dictionary": [],
        "enum": false,
        "entityProperty": false
    }
];

var mockControlledVocabularyRepo3 = [
    {
        "id": 1,
        "position": 1,
        "name": "guarantor",
        "entityName": "Embargo",
        "dictionary": [
            "DEFAULT",
            "PROQUEST"
        ],
        "enum": true,
        "entityProperty": true
    },
    {
        "id": 2,
        "position": 2,
        "name": "type",
        "entityName": "Attachment",
        "dictionary": [],
        "enum": false,
        "entityProperty": true
    },
    {
        "id": 3,
        "position": 3,
        "name": "subjects",
        "entityName": null,
        "dictionary": [],
        "enum": false,
        "entityProperty": false
    }
];

angular.module('mock.controlledVocabularyRepo', []).service('ControlledVocabularyRepo', function() {
    return this;
});