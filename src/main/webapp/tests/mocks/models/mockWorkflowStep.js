var mockWorkflowStep1 = {
    'id': 1
};

var mockWorkflowStep2 = {
    'id': 2
};

var mockWorkflowStep3 = {
    'id': 3
};

var mockWorkflowStep = function($q) {
    var model = mockModel($q, mockWorkflowStep1);

    return model;
};

angular.module('mock.workflowStep', []).service('WorkflowStep', mockWorkflowStep);

