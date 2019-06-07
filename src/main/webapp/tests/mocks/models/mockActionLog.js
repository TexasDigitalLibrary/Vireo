var dataActionLog1 = {
    id: 1,
    actionDate: null,
    entry: "",
    privateFlag: false,
    submissionStatus: {
        id: 1,
        submissionState: "IN_PROGRESS"
    },
    user: {
        anonymous: false,
        email: "aggieJack@library.tamu.edu",
        exp: "1425393875282",
        firstName: "Jack",
        lastName: "Daniels",
        netId: "aggieJack",
        role: "ROLE_ADMIN",
        uin: "123456789"
    }
};

var dataActionLog2 = {
    id: 2,
    actionDate: null,
    entry: "",
    privateFlag: false,
    submissionStatus: {
        id: 2,
        submissionState: "SUBMITTED"
    },
    user: {
        anonymous: false,
        email: "aggieJill@library.tamu.edu",
        exp: "1425393875282",
        firstName: "Jill",
        lastName: "Daniels",
        netId: "aggieJill",
        role: "ROLE_STUDENT",
        uin: "987654321"
    }
};

var dataActionLog3 = {
    id: 3,
    actionDate: null,
    entry: "",
    privateFlag: false,
    submissionStatus: {
        id: 3,
        submissionState: "WITHDRAWN"
    },
    user: {
        anonymous: false,
        email: "jsmith@library.tamu.edu",
        exp: "1425393875282",
        firstName: "Jacob",
        lastName: "Smith",
        netId: "jsmith",
        role: "ROLE_STUDENT",
        uin: "192837465"
    }
};

var dataActionLog4 = {
    id: 4,
    actionDate: null,
    entry: "",
    privateFlag: true,
    submissionStatus: {
        id: 1,
        submissionState: "IN_PROGRESS"
    },
    user: {
        anonymous: false,
        email: "aggieJack@library.tamu.edu",
        exp: "1425393875282",
        firstName: "Jack",
        lastName: "Daniels",
        netId: "aggieJack",
        role: "ROLE_ADMIN",
        uin: "123456789"
    }
};

var dataActionLog5 = {
    id: 5,
    actionDate: null,
    entry: "",
    privateFlag: true,
    submissionStatus: {
        id: 2,
        submissionState: "SUBMITTED"
    },
    user: {
        anonymous: false,
        email: "aggieJill@library.tamu.edu",
        exp: "1425393875282",
        firstName: "Jill",
        lastName: "Daniels",
        netId: "aggieJill",
        role: "ROLE_STUDENT",
        uin: "987654321"
    }
};

var dataActionLog6 = {
    id: 6,
    actionDate: null,
    entry: "",
    privateFlag: true,
    submissionStatus: {
        id: 1,
        submissionState: "IN_PROGRESS"
    },
    user: {
        anonymous: false,
        email: "jsmith@library.tamu.edu",
        exp: "1425393875282",
        firstName: "Jacob",
        lastName: "Smith",
        netId: "jsmith",
        role: "ROLE_STUDENT",
        uin: "192837465"
    }
};

var mockActionLog = function($q) {
    var model = mockModel("ActionLog", $q, dataActionLog1);

    return model;
};

angular.module('mock.actionLog', []).service('ActionLog', mockActionLog);

