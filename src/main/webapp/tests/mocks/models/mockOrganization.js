var mockOrganization1 = {
    'id': 1
};

var mockOrganization2 = {
    'id': 2
};

var mockOrganization3 = {
    'id': 3
};

angular.module('mock.Organization', []).service('Organization', function($q) {
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

    model.isDirty = false;

    model.mock = function(toMock) {
        model.id = toMock.id;
    };

    model.addEmailWorkflowRule = function(templateId, recipient, submissionStatusId) {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    model.changeEmailWorkflowRuleActivation = function(rule) {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
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

    model.editEmailWorkflowRule = function(rule) {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    model.reload = function() {
    };

    model.removeEmailWorkfowRule = function(rule) {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    model.save = function() {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    return model;
});
