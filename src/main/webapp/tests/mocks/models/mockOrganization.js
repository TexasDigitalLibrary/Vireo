var dataOrganization1 = {
    id: 1,
    name: "organization 1"
};

var dataOrganization2 = {
    id: 2,
    name: "organization 2"
};

var dataOrganization3 = {
    id: 3,
    name: "organization 3"
};

var dataOrganization4 = {
    id: 4,
    name: "organization 4"
};

var dataOrganization5 = {
    id: 5,
    name: "organization 5"
};

var dataOrganization6 = {
    id: 6,
    name: "organization 6"
};

var mockOrganization = function($q) {
    var model = mockModel($q, dataOrganization1);

    model.addEmailWorkflowRule = function(templateId, recipient, submissionStatusId) {
        return payloadPromise($q.defer());
    };

    model.changeEmailWorkflowRuleActivation = function(rule) {
        return payloadPromise($q.defer());
    };

    model.editEmailWorkflowRule = function(rule) {
        return payloadPromise($q.defer());
    };

    model.removeEmailWorkflowRule = function(rule) {
        return payloadPromise($q.defer());
    };

    return model;
};

angular.module('mock.organization', []).service('Organization', mockOrganization);

