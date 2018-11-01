var mockFieldValue1 = {
    'id': 1
};

var mockFieldValue2 = {
    'id': 2
};

var mockFieldValue3 = {
    'id': 3
};

angular.module('mock.fieldValue', []).service('FieldValue', function($q) {
    var model = this;
    var defer;
    var payloadResponse = function (payload) {
        return defer.resolve({
            body: angular.toJson({
                meta: {
                    status: 'SUCCESS'
                },
                payload: payload
            })
        });
    };
    var isValid =  false;
    var validationMessages = [];

    model.isDirty = false;

    model.mock = function(toMock) {
        model.id = toMock.id;
    };

    model.addValidationMessage = function(message) {
        validationMessages.push(message);
    };

    model.clearValidationResults = function () {
    };

    model.delete = function() {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    model.dirty = function(boolean) {
        model.isDirty = boolean;
    };

    model.getValidationMessages = function() {
        return angular.copy(validationMessages);
    };

    model.isValid = function() {
        return isValid;
    };

    model.reload = function() {
    };

    model.save = function() {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    model.setIsvalid = function(valid) {
        isValid = valid ? true : false;
    };

    model.setValidationMessages = function(messages) {
        validationMessages.length = 0;
        angular.extend(validationMessages, messages);
    };

    return model;
});
