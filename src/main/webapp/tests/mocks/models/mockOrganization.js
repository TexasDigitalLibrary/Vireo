var dataOrganization1 = {
    id: 1,
    name: "organization 1",
    defaultRecipients: []
};

var dataOrganization2 = {
    id: 2,
    name: "organization 2",
    defaultRecipients: []
};

var dataOrganization3 = {
    id: 3,
    name: "organization 3",
    defaultRecipients: []
};

var dataOrganization4 = {
    id: 4,
    name: "organization 4",
    defaultRecipients: []
};

var dataOrganization5 = {
    id: 5,
    name: "organization 5",
    defaultRecipients: []
};

var dataOrganization6 = {
    id: 6,
    name: "organization 6",
    defaultRecipients: []
};
var mockOrganization = function($q) {
    var model = mockModel("Organization", $q, dataOrganization1);
    
    model.defaultRecipients = [{
        name: "Submitter",
        type: "SUBMITTER",
        data: "Submitter"
      },
      {
        name: "Assignee",
        type: "ASSIGNEE",
        data: "Assignee"
      },
      {
        name: "Organization",
        type: "ORGANIZATION",
        data: null
      }
    ];

    model.addEmailWorkflowRule = function(templateId, recipient, submissionStatusId) {
        return payloadPromise($q.defer());
    };

    
    model.changeEmailWorkflowRuleActivation = function(rule) {
        return payloadPromise($q.defer());
    };

    model.editEmailWorkflowRule = function(rule) {
        return payloadPromise($q.defer());
    };

    model.getWorkflowEmailContacts = function() {
      return model.defaultRecipients;
    };

    model.removeEmailWorkflowRule = function(rule) {
        return payloadPromise($q.defer());
    };

    return model;
};

angular.module('mock.organization', []).service('Organization', mockOrganization);

