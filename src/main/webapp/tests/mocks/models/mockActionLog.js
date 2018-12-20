var mockActionLog1 = {
    'id': 1
};

var mockActionLog2 = {
    'id': 2
};

var mockActionLog3 = {
    'id': 3
};

var mockActionLog = function($q) {
    var model = mockModel($q, mockActionLog1);

    return model;
};

angular.module('mock.actionLog', []).service('ActionLog', mockActionLog);

