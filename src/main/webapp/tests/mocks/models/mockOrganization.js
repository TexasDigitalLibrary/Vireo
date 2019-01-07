var mockOrganization1 = {
    id: 1,
    name: "organization 1"
};

var mockOrganization2 = {
    id: 2,
    name: "organization 2"
};

var mockOrganization3 = {
    id: 3,
    name: "organization 3"
};

var mockOrganization = function($q) {
    var model = mockModel($q, mockOrganization1);

    model.addEmailWorkflowRule = function(templateId, recipient, submissionStatusId) {
        return payloadPromise($q.defer());
    };

    model.changeEmailWorkflowRuleActivation = function(rule) {
        return payloadPromise($q.defer());
    };

    model.editEmailWorkflowRule = function(rule) {
        return payloadPromise($q.defer());
    };

    model.removeEmailWorkfowRule = function(rule) {
        return payloadPromise($q.defer());
    };

    return model;
};

angular.module('mock.organization', []).service('Organization', mockOrganization);

