var dataUser1 = {
    anonymous: false,
    email: "aggieJack@library.tamu.edu",
    exp: "1425393875282",
    firstName: "Jack",
    lastName: "Daniels",
    netId: "aggieJack",
    role: "ROLE_ADMIN",
    uin: "123456789"
};

var dataUser2 = {
    anonymous: false,
    email: "aggieJill@library.tamu.edu",
    exp: "1425393875282",
    firstName: "Jill",
    lastName: "Daniels",
    netId: "aggieJill",
    role: "ROLE_STUDENT",
    uin: "987654321"
};

var dataUser3 = {
    anonymous: false,
    email: "jsmith@library.tamu.edu",
    exp: "1425393875282",
    firstName: "Jacob",
    lastName: "Smith",
    netId: "jsmith",
    role: "ROLE_STUDENT",
    uin: "192837465"
};

var dataUser4 = {
    anonymous: false,
    email: "aggieJack@library.tamu.edu",
    exp: "1425393875282",
    firstName: "Test",
    lastName: "User1",
    netId: "user1",
    role: "ROLE_ADMIN",
    uin: "111111111"
};

var dataUser5 = {
    anonymous: false,
    email: "aggieJack@library.tamu.edu",
    exp: "1425393875282",
    firstName: "Test",
    lastName: "User2",
    netId: "user2",
    role: "ROLE_STUDENT",
    uin: "222222222"
};

var dataUser6 = {
    anonymous: false,
    email: "aggieJack@library.tamu.edu",
    exp: "1425393875282",
    firstName: "Test",
    lastName: "User3",
    netId: "user3",
    role: "ROLE_STUDENT",
    uin: "333333333"
};

var mockUser = function($q) {
    var model = mockModel($q, dataUser1);

    return model;
};

angular.module('mock.user', []).service('User', mockUser);
