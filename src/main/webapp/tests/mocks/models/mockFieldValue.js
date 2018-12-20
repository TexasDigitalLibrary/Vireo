var mockFieldValue1 = {
    'id': 1
};

var mockFieldValue2 = {
    'id': 2
};

var mockFieldValue3 = {
    'id': 3
};

var mockFieldValue = function($q) {
    var model = mockModel($q, mockFieldValue1);
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

