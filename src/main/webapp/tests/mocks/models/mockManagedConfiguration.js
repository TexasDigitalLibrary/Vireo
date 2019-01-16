var dataManagedConfiguration1 = {
    id: 1,
    name: "mc1",
    type: null,
    value: ""
};

var dataManagedConfiguration2 = {
    id: 2,
    name: "mc2",
    type: null,
    value: ""
};

var dataManagedConfiguration3 = {
    id: 3,
    name: "mc3",
    type: null,
    value: ""
};

var dataManagedConfiguration4 = {
    id: 4,
    name: "mc4",
    type: null,
    value: ""
};

var dataManagedConfiguration5 = {
    id: 5,
    name: "mc5",
    type: null,
    value: ""
};

var dataManagedConfiguration6 = {
    id: 6,
    name: "mc6",
    type: null,
    value: ""
};

var mockManagedConfiguration = function($q) {
    var model = mockModel("ManagedConfiguration", $q, dataManagedConfiguration1);

    model.reset = function() {
        return payloadPromise($q.defer());
    };

    return model;
};

angular.module('mock.managedConfiguration', []).service('ManagedConfiguration', mockManagedConfiguration);

