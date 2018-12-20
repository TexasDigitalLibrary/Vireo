var mockOrganizationRepo1 = [
    {
        "id": 1
    },
    {
        "id": 2
    },
    {
        "id": 3
    }
];

var mockOrganizationRepo2 = [
    {
        "id": 1
    },
    {
        "id": 2
    },
    {
        "id": 3
    }
];

var mockOrganizationRepo3 = [
    {
        "id": 1
    },
    {
        "id": 2
    },
    {
        "id": 3
    }
];

angular.module('mock.organizationRepo', []).service('OrganizationRepo', function($q) {
    var repo = mockRepo('OrganizationRepo', $q, mockOrganization, mockOrganizationRepo1);

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
        var payload = {};
        if (repo.submissionsCount.hasOwnProperty(orgId)) {
            payload = repo.submissionsCount[orgId];
        }
        return payloadPromise($q.defer(), payload);
    };

    repo.create = function (model) {
        model.id = repo.list.length + 1;
        repo.list.push(angular.copy(model));
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
                found = angular.copy(repo.mockedList[i]);
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
