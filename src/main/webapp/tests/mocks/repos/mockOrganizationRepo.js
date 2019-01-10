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

angular.module('mock.organizationRepo', []).service('OrganizationRepo', function($q) {
    var repo = mockRepo('OrganizationRepo', $q, mockOrganization, dataOrganizationRepo1);

    repo.newOrganization = {};
    repo.selectedId = null;
    repo.submissionsCount = {};

    repo.addWorkflowStep = function (workflowStep) {
        var payload = {};
        // TODO
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
        model.id = repo.list.length + 1;
        repo.list.push(repo.mockCopy(model));
        return payloadPromise($q.defer(), model);
    };

    repo.deleteWorkflowStep = function (workflowStep) {
        var payload = {};
        repo.clearValidationResults();
        // TODO
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

    repo.reorderWorkflowStep = function (upOrDown, workflowStepID) {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    repo.resetNewOrganization = function () {
        for (var key in repo.newOrganization) {
            if (key !== 'category' && key !== 'parent') {
                delete repo.newOrganization[key];
            }
        }
        return repo.newOrganization;
    };

    repo.restoreDefaults = function (organization) {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    repo.setSelectedOrganization = function (organization) {
        repo.selectedId = organization.id;
        return organization;
    };

    repo.updateWorflowStep = function (workflowStep) {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    return repo;
});
