var dataFieldProfile1 = {
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
        value: "_doctype_primary"
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
};

var dataFieldProfile2 = {
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
        id: 3,
        documentTypePredicate: true,
        value: "text/plain"
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
};

var dataFieldProfile3 = {
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
        value: "text/plain"
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
};

var dataFieldProfile4 = {
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
        id: 2,
        documentTypePredicate: false,
        value: "_doctype_archived"
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
};

var dataFieldProfile5 = {
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
        value: "_doctype_primary"
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
};

var dataFieldProfile6 = {
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
        value: "text/plain"
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
};

var mockFieldProfile = function($q) {
    var model = mockModel("FieldProfile", $q, dataFieldProfile1);

    return model;
};

angular.module('mock.fieldProfile', []).service('FieldProfile', mockFieldProfile);
