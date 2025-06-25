var dataOrganizationRepo1 = [
    dataOrganization1,
    dataOrganization2,
    dataOrganization3
];

var dataOrganizationRepo2 = [
    dataOrganization3,
    dataOrganization2,
    dataOrganization1
];

var dataOrganizationRepo3 = [
    dataOrganization4,
    dataOrganization5,
    dataOrganization6
];

angular.module("mock.organizationRepo", []).service("OrganizationRepo", function($q) {
    var repo = mockRepo("OrganizationRepo", $q, mockOrganization, dataOrganizationRepo1);

    repo.newOrganization = {};
    repo.selectedId = null;
    repo.submissionsCount = {};

    repo.addEmailWorkflowRule = function (organization, template, recipient, submissionStatus) {
        var payload = {};
        return payloadPromise($q.defer(), payload);
    };

    repo.addEmailWorkflowRuleByAction = function (organization, template, recipient, submissionStatus) {
        var payload = {};
        return payloadPromise($q.defer(), payload);
    };

    repo.addWorkflowStep = function (workflowStep) {
        var payload = {};
        return payloadPromise($q.defer(), payload);
    };

    repo.changeEmailWorkflowRuleActivation = function (organization, emailWorkflowRule) {
        var payload = {};
        return payloadPromise($q.defer(), payload);
    };

    repo.changeEmailWorkflowRuleByActionActivation = function (organization, emailWorkflowRule) {
        var payload = {};
        return payloadPromise($q.defer(), payload);
    };

    repo.clearValidationResults = function () {
        validationResults = {};
    };

    repo.countSubmissions = function (orgId) {
        var payload = 0;

        if (repo.submissionsCount.hasOwnProperty(orgId)) {
            payload = repo.submissionsCount[orgId];
        }

        return valuePromise($q.defer(), payload);
    };

    repo.create = function (model) {
        model.id = repo.mockedList.length + 1;
        repo.mockedList.push(repo.mockCopy(model));
        return payloadPromise($q.defer(), model);
    };

    repo.deleteById = function (orgId) {
        var payload = {};

        return payloadPromise($q.defer(), payload);
    };

    repo.deleteWorkflowStep = function (workflowStep) {
        var payload = {};
        repo.clearValidationResults();

        // FIXME: $scope.deleteWorkflowStep of organizationManagementController is expecting a different response structure.
        var response = {
            meta: {
                status: "SUCCESS",
            },
            payload: payload,
            status: 200
        };

        return valuePromise($q.defer(), response);
    };

    repo.editEmailWorkflowRule = function (organization, emailWorkflowRule) {
        var payload = {};
        return payloadPromise($q.defer(), payload);
    };

    repo.editEmailWorkflowRuleByAction = function (organization, emailWorkflowRule) {
        var payload = {};
        return payloadPromise($q.defer(), payload);
    };

    repo.getAllSpecific = function (specific) {
        var payload = {};
        return payloadPromise($q.defer(), payload);
    };

    repo.getById = function (id, specific) {
        var payload = {};
        return payloadPromise($q.defer(), payload);
    };

    repo.getNewOrganization = function () {
        return repo.newOrganization;
    };

    repo.getSelectedOrganization = function () {
        var found;
        for (var i in repo.mockedList) {
            if (repo.mockedList[i].id === repo.selectedId) {
                found = repo.mockCopy(repo.mockedList[i]);
            }
        }
        return found;
    };

    repo.getSelectedOrganizationId = function () {
        return repo.selectedId;
    };

    repo.removeEmailWorkflowRule = function (organization, emailWorkflowRule) {
        var payload = {};
        return payloadPromise($q.defer(), payload);
    };

    repo.removeEmailWorkflowRuleByAction = function (organization, emailWorkflowRule) {
        var payload = {};
        return payloadPromise($q.defer(), payload);
    };

    repo.reorderWorkflowSteps = function (upOrDown, workflowStepID) {
        var payload = {};
        return payloadPromise($q.defer(), payload);
    };

    repo.resetNewOrganization = function () {
        for (var key in repo.newOrganization) {
            if (key !== "category" && key !== "parent") {
                delete repo.newOrganization[key];
            }
        }
        return repo.newOrganization;
    };

    repo.restoreDefaults = function (organization) {
        var payload = {};

        // FIXME: $scope.restoreOrganizationDefaults of organizationManagementController is expecting a different response structure.
        var response = {
            meta: {
                status: "SUCCESS",
            },
            payload: payload,
            status: 200
        };

        return valuePromise($q.defer(), response);
    };

    repo.setSelectedOrganization = function (organization) {
        repo.selectedId = organization.id;
        return organization;
    };

    repo.updateWorkflowStep = function (workflowStep) {
        var payload = {};
        return payloadPromise($q.defer(), payload);
    };

    return repo;
});
