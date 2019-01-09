var dataControlledVocabulary1 = [
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

var dataControlledVocabulary2 = [
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

var dataControlledVocabulary3 = [
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

var mockControlledVocabulary = function($q) {
    var model = mockModel($q, dataControlledVocabulary1);

    return model;
};

angular.module('mock.controlledVocabulary', []).service('ControlledVocabulary', mockControlledVocabulary);

