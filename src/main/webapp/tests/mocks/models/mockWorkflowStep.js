var dataWorkflowStep1 = {
    id: 1
};

var dataWorkflowStep2 = {
    id: 2
};

var dataWorkflowStep3 = {
    id: 3
};

var dataWorkflowStep4 = {
    id: 4
};

var dataWorkflowStep5 = {
    id: 5
};

var dataWorkflowStep6 = {
    id: 6
};

var mockWorkflowStep = function($q) {
    var model = mockModel($q, dataWorkflowStep1);

    return model;
};

angular.module('mock.workflowStep', []).service('WorkflowStep', mockWorkflowStep);

