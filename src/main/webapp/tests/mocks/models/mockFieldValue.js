var dataFieldValue1 = {
    id: 1
};

var dataFieldValue2 = {
    id: 2
};

var dataFieldValue3 = {
    id: 3
};

var dataFieldValue4 = {
    id: 4
};

var dataFieldValue5 = {
    id: 5
};

var dataFieldValue6 = {
    id: 6
};


var mockFieldValue = function($q) {
    var model = mockModel($q, dataFieldValue1);
    var isValid =  false;
    var validationMessages = [];

    model.addValidationMessage = function(message) {
        validationMessages.push(message);
    };

    model.getValidationMessages = function() {
        return angular.copy(validationMessages);
    };

    model.setIsvalid = function(valid) {
        isValid = valid ? true : false;
    };

    model.setValidationMessages = function(messages) {
        validationMessages.length = 0;
        angular.extend(validationMessages, messages);
    };

    return model;
};

angular.module('mock.fieldValue', []).service('FieldValue', mockFieldValue);

