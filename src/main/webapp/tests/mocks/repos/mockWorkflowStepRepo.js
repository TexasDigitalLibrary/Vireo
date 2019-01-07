var mockWorkflowStepRepo1 = [
    {
        id: 1
    },
    {
        id: 2
    },
    {
        id: 3
    }
];

var mockWorkflowStepRepo2 = [
    {
        id: 1
    },
    {
        id: 2
    },
    {
        id: 3
    }
];

var mockWorkflowStepRepo3 = [
    {
        id: 1
    },
    {
        id: 2
    },
    {
        id: 3
    }
];

angular.module('mock.workflowStepRepo', []).service('WorkflowStepRepo', function($q) {
    var repo = mockRepo('WorkflowStepRepo', $q, mockWorkflowStep, mockWorkflowStepRepo1);

    repo.addFieldProfile = function (workflowStep, fieldProfile) {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    repo.addNote = function (workflowStep, note) {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    repo.removeFieldProfile = function (workflowStep, fieldProfile) {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    repo.removeNote = function (workflowStep, note) {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    repo.reorderFieldProfile = function (workflowStep, src, dest) {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    repo.reorderNote = function (workflowStep, src, dest) {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    repo.updateFieldProfile = function (workflowStep, fieldProfile) {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    repo.updateNote = function (workflowStep, note) {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    return repo;
});
