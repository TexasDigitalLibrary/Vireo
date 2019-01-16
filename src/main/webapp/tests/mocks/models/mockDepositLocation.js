var dataDepositLocation1 = {
    id: 1,
    position: 1,
    name: "Test0",
    repository: "Dspace",
    collection: null,
    username: "test@tdl.org",
    password: "abc123",
    onBehalfOf: "TDL",
    packager: "VireoExport",
    depositor: "Sword1Deposit",
    timeout: 100
};

var dataDepositLocation2 = {
    id: 2,
    position: 2,
    name: "Test1",
    repository: "Fedora",
    collection: null,
    username: "test@tdl.org",
    password: "abc123",
    onBehalfOf: "Texas A&M",
    packager: "VireoExport",
    depositor: "Sword1Deposit",
    timeout: 200
};

var dataDepositLocation3 = {
    id: 3,
    position: 3,
    name: "Test2",
    repository: "Nuxio",
    collection: null,
    username: "test@tdl.org",
    password: "abc123",
    onBehalfOf: "Texas A&M",
    packager: "VireoExport",
    depositor: "FileDeposit",
    timeout: 300
};

var dataDepositLocation4 = {
    id: 4,
    position: 1,
    name: "Test3",
    repository: "Dspace",
    collection: null,
    username: "test@tdl.org",
    password: "abc123",
    onBehalfOf: "TDL",
    packager: "VireoExport",
    depositor: "Sword1Deposit",
    timeout: 100
};

var dataDepositLocation5 = {
    id: 5,
    position: 2,
    name: "Test4",
    repository: "Fedora",
    collection: null,
    username: "test@tdl.org",
    password: "abc123",
    onBehalfOf: "Texas A&M",
    packager: "VireoExport",
    depositor: "Sword1Deposit",
    timeout: 200
};

var dataDepositLocation6 = {
    id: 6,
    position: 3,
    name: "Test5",
    repository: "Nuxio",
    collection: null,
    username: "test@tdl.org",
    password: "abc123",
    onBehalfOf: "Texas A&M",
    packager: "VireoExport",
    depositor: "FileDeposit",
    timeout: 300
};

var mockDepositLocation = function($q) {
    var model = mockModel("DepositLocation", $q, dataDepositLocation1);

    model.testConnection = function() {
        return payloadPromise($q.defer());
    };

    return model;
};

angular.module('mock.depositLocation', []).service('DepositLocation', mockDepositLocation);

