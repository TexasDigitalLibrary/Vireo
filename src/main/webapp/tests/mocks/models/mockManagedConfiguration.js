var dataManagedConfiguration1 = {
    id: 1,
    value: ""
};

var dataManagedConfiguration2 = {
    id: 2,
    value: ""
};

var dataManagedConfiguration3 = {
    id: 3,
    value: ""
};

var mockManagedConfiguration = function($q) {
    var model = mockModel($q, dataManagedConfiguration1);

    model.reset = function() {
        return payloadPromise($q.defer());
    };

    return model;
};

angular.module('mock.managedConfiguration', []).service('ManagedConfiguration', mockManagedConfiguration);

