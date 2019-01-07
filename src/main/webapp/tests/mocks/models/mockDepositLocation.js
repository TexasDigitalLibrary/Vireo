var mockDepositLocation1 = {
    id: 1
};

var mockDepositLocation2 = {
    id: 2
};

var mockDepositLocation3 = {
    id: 3
};

var mockDepositLocation = function($q) {
    var model = mockModel($q, mockDepositLocation1);

    model.testConnection = function() {
        return payloadPromise($q.defer());
    };

    return model;
};

angular.module('mock.depositLocation', []).service('DepositLocation', mockDepositLocation);

