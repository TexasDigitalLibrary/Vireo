var dataWorkflowStepRepo1 = [
    dataWorkflowStep1,
    dataWorkflowStep2,
    dataWorkflowStep3
];

var dataWorkflowStepRepo2 = [
    dataWorkflowStep3,
    dataWorkflowStep2,
    dataWorkflowStep1
];

var dataWorkflowStepRepo3 = [
    dataWorkflowStep4,
    dataWorkflowStep5,
    dataWorkflowStep6
];

angular.module('mock.workflowStepRepo', []).service('WorkflowStepRepo', function($q) {
    var repo = mockRepo('WorkflowStepRepo', $q, mockWorkflowStep, dataWorkflowStepRepo1);

    repo.addFieldProfile = function (workflowStep, fieldProfile) {
        var payload = {};
        return payloadPromise($q.defer(), payload);
    };

    repo.addNote = function (workflowStep, note) {
        var payload = {};
        return payloadPromise($q.defer(), payload);
    };

    repo.removeFieldProfile = function (workflowStep, fieldProfile) {
        var payload = {};
        return payloadPromise($q.defer(), payload);
    };

    repo.removeNote = function (workflowStep, note) {
        var payload = {};
        return payloadPromise($q.defer(), payload);
    };

    repo.reorderFieldProfile = function (workflowStep, src, dest) {
        var payload = {};
        return payloadPromise($q.defer(), payload);
    };

    repo.reorderNote = function (workflowStep, src, dest) {
        var payload = {};
        return payloadPromise($q.defer(), payload);
    };

    repo.updateFieldProfile = function (workflowStep, fieldProfile) {
        var payload = {};
        return payloadPromise($q.defer(), payload);
    };

    repo.updateNote = function (workflowStep, note) {
        var payload = {};
        return payloadPromise($q.defer(), payload);
    };

    return repo;
});
