var dataFieldValue1 = {
    id: 1,
    contacts: [],
    definition: "",
    fieldPredicate: {
        id: 1,
        documentTypePredicate: false,
        value: "_doctype_primary"
    },
    identifier: "",
    value: "dataFieldValue1"
};

var dataFieldValue2 = {
    id: 2,
    contacts: [],
    definition: "",
    fieldPredicate: {
        id: 2,
        documentTypePredicate: false,
        value: "_doctype_archived"
    },
    fileInfo: {
        name: "test"
    },
    identifier: "",
    value: "dataFieldValue2"
};

var dataFieldValue3 = {
    id: 3,
    contacts: [],
    definition: "",
    fieldPredicate: null,
    identifier: "",
    value: "dataFieldValue3"
};

var dataFieldValue4 = {
    id: 4,
    contacts: [],
    definition: "",
    fieldPredicate: {
        id: 3,
        documentTypePredicate: true,
        value: "text/plain"
    },
    identifier: "",
    value: "dataFieldValue4"
};

var dataFieldValue5 = {
    id: 5,
    contacts: [],
    definition: "",
    fieldPredicate: {
        id: 4,
        documentTypePredicate: true,
        value: "application/pdf"
    },
    identifier: "",
    value: "dataFieldValue5"
};

var dataFieldValue6 = {
    id: 6,
    contacts: [],
    definition: "",
    fieldPredicate: {
        id: 5,
        documentTypePredicate: true,
        value: "text/csv"
    },
    fileInfo: {
        name: "test.csv"
    },
    identifier: "",
    value: "dataFieldValue6"
};


var mockFieldValue = function($q) {
    var model = mockModel("FieldValue", $q, dataFieldValue1);
    var isValid =  false;
    var validationMessages = [];

    model.addValidationMessage = function(message) {
        validationMessages.push(message);
    };

    model.getValidationMessages = function() {
        return angular.copy(validationMessages);
    };

    model.isValid = function() {
        return isValid;
    };

    model.setIsValid = function(valid) {
        isValid = valid ? true : false;
    };

    model.setValidationMessages = function(messages) {
        validationMessages.length = 0;
        angular.extend(validationMessages, messages);
    };

    return model;
};

angular.module("mock.fieldValue", []).service("FieldValue", mockFieldValue);

