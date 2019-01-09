var dataActionLog1 = {
    id: 1
};

var dataActionLog2 = {
    id: 2
};

var dataActionLog3 = {
    id: 3
};

var mockActionLog = function($q) {
    var model = mockModel($q, dataActionLog1);

    return model;
};

angular.module('mock.actionLog', []).service('ActionLog', mockActionLog);

