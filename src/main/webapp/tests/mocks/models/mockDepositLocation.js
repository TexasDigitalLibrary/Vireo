var dataDepositLocation1 = {
    id: 1
};

var dataDepositLocation2 = {
    id: 2
};

var dataDepositLocation3 = {
    id: 3
};

var mockDepositLocation = function($q) {
    var model = mockModel($q, dataDepositLocation1);

    model.testConnection = function() {
        return payloadPromise($q.defer());
    };

    return model;
};

angular.module('mock.depositLocation', []).service('DepositLocation', mockDepositLocation);

