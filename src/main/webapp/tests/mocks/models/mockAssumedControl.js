var dataAssumedControl1 = {
    id: 1,
    user: {
        "uin": "123456789",
        "lastName": "Daniels",
        "firstName": "Jack",
        "role": "ROLE_ADMIN"
    },
    netid: "",
    button: "Unassume",
    status: ""
};

var dataAssumedControl2 = {
    id: 2,
    user: {
        "uin": "987654321",
        "lastName": "Daniels",
        "firstName": "Jill",
        "role": "USER"
    },
    netid: "",
    button: "Unassume",
    status: ""
};

var dataAssumedControl3 = {
    id: 3,
    user: {},
    netid: "",
    button: "Assume",
    status: ""
};

var dataAssumedControl4 = {
    id: 4,
    user: {
        "uin": "123456789",
        "lastName": "Daniels",
        "firstName": "Jack",
        "role": "ROLE_ADMIN"
    },
    netid: "",
    button: "Unassume",
    status: ""
};

var dataAssumedControl5 = {
    id: 5,
    user: {
        "uin": "987654321",
        "lastName": "Daniels",
        "firstName": "Jill",
        "role": "USER"
    },
    netid: "",
    button: "Unassume",
    status: ""
};

var dataAssumedControl6 = {
    id: 6,
    user: {},
    netid: "",
    button: "Assume",
    status: ""
};

var mockAssumedControl = function($q) {
    var model = mockModel("AssumedControl", $q, dataAssumedControl1);

    return model;
};

angular.module("mock.assumedControl", []).service("AssumedControl", mockAssumedControl);

