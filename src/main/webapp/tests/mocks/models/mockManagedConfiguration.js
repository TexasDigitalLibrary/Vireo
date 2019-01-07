var mockManagedConfiguration1 = {
    id: 1,
    value: ""
};

var mockManagedConfiguration2 = {
    id: 2,
    value: ""
};

var mockManagedConfiguration3 = {
    id: 3,
    value: ""
};

var mockManagedConfiguration = function($q) {
    var model = mockModel($q, mockManagedConfiguration1);

    model.reset = function() {
        return payloadPromise($q.defer());
    };

    return model;
};

angular.module('mock.managedConfiguration', []).service('ManagedConfiguration', mockManagedConfiguration);

