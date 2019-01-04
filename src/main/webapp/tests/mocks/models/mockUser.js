var mockUser1 = {
    anonymous: false,
    lastName: "Daniels",
    firstName: "Jack",
    uin: "123456789",
    exp: "1425393875282",
    email: "aggieJack@library.tamu.edu",
    role: "ROLE_ADMIN",
    netId: "aggieJack"
};

var mockUser2 = {
    anonymous: false,
    lastName: "Daniels",
    firstName: "Jill",
    uin: "987654321",
    exp: "1425393875282",
    email: "aggieJill@library.tamu.edu",
    role: "ROLE_STUDENT",
    netId: "aggieJill"
};

var mockUser3 = {
    anonymous: false,
    lastName: "Smith",
    firstName: "Jacob",
    uin: "192837465",
    exp: "1425393875282",
    email: "jsmith@library.tamu.edu",
    role: "ROLE_STUDENT",
    netId: "jsmith"
};

var mockUser = function($q) {
    var model = mockModel($q, mockUser1);

    return model;
};

angular.module('mock.user', []).service('User', mockUser);
