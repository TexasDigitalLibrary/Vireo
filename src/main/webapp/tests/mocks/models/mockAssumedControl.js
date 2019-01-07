var mockAssumedControl1 = {
    user: {
        "uin": "123456789",
        "lastName": "Daniels",
        "firstName": "Jack",
        "role": "ROLE_ADMIN"
    },
    netid: '',
    button: 'Unassume',
    status: ''
};

var mockAssumedControl2 = {
    user: {
        "uin": "987654321",
        "lastName": "Daniels",
        "firstName": "Jill",
        "role": "USER"
    },
    netid: '',
    button: 'Unassume',
    status: ''
};

var mockAssumedControl3 = {
    user: {},
    netid: '',
    button: 'Assume',
    status: ''
};

var mockAssumedControl = function($q) {
    var model = mockModel($q, mockAssumedControl1);

    return model;
};

angular.module('mock.assumedControl', []).service('AssumedControl', mockAssumedControl);

