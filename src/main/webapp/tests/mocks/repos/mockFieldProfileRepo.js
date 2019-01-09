var mockFieldProfileRepo1 = [
    {
        id: 1,
        controlledVocabulary: {
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
        defaultValue: null,
        enabled: true,
        flagged: false,
        fieldPredicate: {
            id: 1,
            documentTypePredicate: false,
            value: "Field Predicate 1"
        },
        gloss: "",
        help: "",
        hidden: false,
        inputType: {
            id: 1,
            name: "INPUT_TEXT"
        },
        logged: false,
        managedConfiguration: {
            id: 1,
            value: ""
        },
        optional: true,
        overrideable: true,
        repeatable: false
    },
    {
        id: 2,
        controlledVocabulary: {
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
        defaultValue: "default",
        enabled: false,
        flagged: true,
        fieldPredicate: {
            id: 1,
            documentTypePredicate: false,
            value: "Field Predicate 1"
        },
        gloss: "",
        help: "",
        hidden: false,
        inputType: {
            id: 1,
            name: "INPUT_TEXT"
        },
        logged: false,
        managedConfiguration: {
            id: 2,
            value: ""
        },
        optional: true,
        overrideable: true,
        repeatable: false
    },
    {
        id: 3,
        controlledVocabulary: {
            id: 2,
            position: 2,
            name: "type",
            entityName: "Attachment",
            dictionary: [],
            enum: false,
            entityProperty: true
        },
        defaultValue: null,
        enabled: true,
        flagged: true,
        fieldPredicate: {
            id: 3,
            documentTypePredicate: true,
            value: "Field Predicate 3"
        },
        gloss: "Field Gloss",
        help: "This is help text.",
        hidden: false,
        inputType: {
            id: 2,
            name: "INPUT_FILE"
        },
        logged: false,
        managedConfiguration: {
            id: 1,
            value: ""
        },
        optional: false,
        overrideable: false,
        repeatable: true
    }
];

var mockFieldProfileRepo2 = [
    {
        id: 4,
        controlledVocabulary: {
            id: 3,
            position: 3,
            name: "test",
            entityName: null,
            dictionary: [],
            enum: false,
            entityProperty: false
        },
        defaultValue: null,
        enabled: false,
        flagged: false,
        fieldPredicate: {
            id: 1,
            documentTypePredicate: false,
            value: "Field Predicate 1"
        },
        gloss: "",
        help: "",
        hidden: false,
        inputType: {
            id: 1,
            name: "INPUT_TEXT"
        },
        logged: false,
        managedConfiguration: {
            id: 1,
            value: ""
        },
        optional: false,
        overrideable: false,
        repeatable: false
    },
    {
        id: 5,
        controlledVocabulary: {
            id: 3,
            position: 3,
            name: "test",
            entityName: null,
            dictionary: [],
            enum: false,
            entityProperty: false
        },
        defaultValue: "default",
        enabled: true,
        flagged: true,
        fieldPredicate: {
            id: 1,
            documentTypePredicate: false,
            value: "Field Predicate 1"
        },
        gloss: "",
        help: "",
        hidden: false,
        inputType: {
            id: 1,
            name: "INPUT_TEXT"
        },
        logged: false,
        managedConfiguration: {
            id: 2,
            value: ""
        },
        optional: true,
        overrideable: false,
        repeatable: false
    },
    {
        id: 6,
        controlledVocabulary: {
            id: 3,
            position: 3,
            name: "test",
            entityName: null,
            dictionary: [],
            enum: false,
            entityProperty: false
        },
        defaultValue: null,
        enabled: true,
        flagged: true,
        fieldPredicate: {
            id: 3,
            documentTypePredicate: true,
            value: "Field Predicate 3"
        },
        gloss: "Field Gloss",
        help: "This is help text.",
        hidden: false,
        inputType: {
            id: 2,
            name: "INPUT_FILE"
        },
        logged: false,
        managedConfiguration: {
            id: 1,
            value: ""
        },
        optional: false,
        overrideable: true,
        repeatable: false
    }
];

var mockFieldProfileRepo3 = [
    {
        id: 1,
        controlledVocabulary: {
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
        defaultValue: null,
        enabled: true,
        flagged: false,
        fieldPredicate: {
            id: 1,
            documentTypePredicate: false,
            value: "Field Predicate 1"
        },
        gloss: "",
        help: "",
        hidden: false,
        inputType: {
            id: 1,
            name: "INPUT_TEXT"
        },
        logged: false,
        managedConfiguration: {
            id: 1,
            value: ""
        },
        optional: true,
        overrideable: true,
        repeatable: false
    },
    {
        id: 2,
        controlledVocabulary: {
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
        defaultValue: "default",
        enabled: false,
        flagged: true,
        fieldPredicate: {
            id: 1,
            documentTypePredicate: false,
            value: "Field Predicate 1"
        },
        gloss: "",
        help: "",
        hidden: false,
        inputType: {
            id: 1,
            name: "INPUT_TEXT"
        },
        logged: false,
        managedConfiguration: {
            id: 2,
            value: ""
        },
        optional: true,
        overrideable: true,
        repeatable: false
    },
    {
        id: 6,
        controlledVocabulary: {
            id: 3,
            position: 3,
            name: "test",
            entityName: null,
            dictionary: [],
            enum: false,
            entityProperty: false
        },
        defaultValue: null,
        enabled: true,
        flagged: true,
        fieldPredicate: {
            id: 3,
            documentTypePredicate: true,
            value: "Field Predicate 3"
        },
        gloss: "Field Gloss",
        help: "This is help text.",
        hidden: false,
        inputType: {
            id: 2,
            name: "INPUT_FILE"
        },
        logged: false,
        managedConfiguration: {
            id: 1,
            value: ""
        },
        optional: false,
        overrideable: true,
        repeatable: false
    }
];

angular.module('mock.fieldProfileRepo', []).service('FieldProfileRepo', function($q) {
    var repo = mockRepo('FieldProfileRepo', $q, mockFieldProfile, mockFieldProfileRepo1);

    return repo;
});
