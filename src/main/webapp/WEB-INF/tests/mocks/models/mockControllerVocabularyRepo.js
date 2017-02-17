var mockControlledVocabularyRepo1 = [
    {
        "id": 1,
        "position": 1,
        "name": "guarantor",
        "entityName": "Embargo",
        "language": {
            "id": 1,
            "position": null,
            "name": "English"
        },
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
        "language": {
            "id": 1,
            "position": null,
            "name": "English"
        },
        "dictionary": [],
        "enum": false,
        "entityProperty": true
    },
    {
        "id": 3,
        "position": 3,
        "name": "test",
        "entityName": null,
        "language": {
            "id": 1,
            "position": null,
            "name": "English"
        },
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
        "language": {
            "id": 1,
            "position": null,
            "name": "English"
        },
        "dictionary": [],
        "enum": true,
        "entityProperty": true
    },
    {
        "id": 2,
        "position": 2,
        "name": "type",
        "entityName": "Attachment",
        "language": {
            "id": 1,
            "position": null,
            "name": "English"
        },
        "dictionary": [],
        "enum": false,
        "entityProperty": true
    },
    {
        "id": 3,
        "position": 3,
        "name": "test",
        "entityName": null,
        "language": {
            "id": 1,
            "position": null,
            "name": "English"
        },
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
        "language": {
            "id": 1,
            "position": null,
            "name": "English"
        },
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
        "language": {
            "id": 1,
            "position": null,
            "name": "English"
        },
        "dictionary": [],
        "enum": false,
        "entityProperty": true
    },
    {
        "id": 3,
        "position": 3,
        "name": "subjects",
        "entityName": null,
        "language": {
            "id": 1,
            "position": null,
            "name": "English"
        },
        "dictionary": [],
        "enum": false,
        "entityProperty": false
    }
];

angular.module('mock.controlledVocabularyRepo', []).
    service('ControlledVocabularyRepo', function($q) {

      var self;


        return ControlledVocabularyRepo;
});
