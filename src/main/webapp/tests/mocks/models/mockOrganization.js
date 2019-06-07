var dataOrganization1 = {
    id: 1,
    acceptsSubmissions: true,
    aggregateWorkflowSteps: [],
    category: {
        id: 1,
        name: "OrganizationCategory1"
    },
    childrenOrganizations: [],
    defaultRecipients: [],
    emails: [],
    emailWorkflowRules: [],
    name: "organization 1",
    originalWorkflowSteps: [],
    parentOrganization: null
};

var dataOrganization2 = {
    id: 2,
    acceptsSubmissions: false,
    aggregateWorkflowSteps: [],
    category: {
        id: 1,
        name: "OrganizationCategory1"
    },
    childrenOrganizations: [],
    defaultRecipients: [],
    emails: [],
    emailWorkflowRules: [],
    name: "organization 2",
    originalWorkflowSteps: [],
    parentOrganization: null
};

var dataOrganization3 = {
    id: 3,
    acceptsSubmissions: true,
    aggregateWorkflowSteps: [],
    category: {
        id: 2,
        name: "OrganizationCategory2"
    },
    childrenOrganizations: [],
    defaultRecipients: [],
    emails: [],
    emailWorkflowRules: [],
    name: "organization 3",
    originalWorkflowSteps: [],
    parentOrganization: null
};

var dataOrganization4 = {
    id: 4,
    acceptsSubmissions: false,
    aggregateWorkflowSteps: [],
    category: {
        id: 2,
        name: "OrganizationCategory2"
    },
    childrenOrganizations: [],
    defaultRecipients: [],
    emails: [],
    emailWorkflowRules: [],
    name: "organization 4",
    originalWorkflowSteps: [],
    parentOrganization: null
};

var dataOrganization5 = {
    id: 5,
    acceptsSubmissions: true,
    aggregateWorkflowSteps: [],
    category: {
        id: 3,
        name: "OrganizationCategory3"
    },
    childrenOrganizations: [],
    defaultRecipients: [],
    emails: [],
    emailWorkflowRules: [],
    name: "organization 5",
    originalWorkflowSteps: [],
    parentOrganization: null
};

var dataOrganization6 = {
    id: 6,
    acceptsSubmissions: false,
    aggregateWorkflowSteps: [],
    category: {
        id: 3,
        name: "OrganizationCategory3"
    },
    childrenOrganizations: [],
    defaultRecipients: [],
    emails: [],
    emailWorkflowRules: [],
    name: "organization 6",
    originalWorkflowSteps: [],
    parentOrganization: null
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

