var dataWorkflowStep1 = {
    id: 1
};

var dataWorkflowStep2 = {
    id: 2,
    aggregateFieldProfiles: [
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
        }
    ]
};

var dataWorkflowStep3 = {
    id: 3
};

var dataWorkflowStep4 = {
    id: 4
};

var dataWorkflowStep5 = {
    id: 5
};

var dataWorkflowStep6 = {
    id: 6
};

var mockWorkflowStep = function($q) {
    var model = mockModel("WorkflowStep", $q, dataWorkflowStep1);

    return model;
};

angular.module('mock.workflowStep', []).service('WorkflowStep', mockWorkflowStep);

