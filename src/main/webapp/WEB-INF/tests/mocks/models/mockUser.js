var mockUser1 = {
    "lastName": "Daniels",
    "firstName": "Jack",
    "uin": "123456789",
    "exp": "1425393875282",
    "email": "aggieJack@library.tamu.edu",
    "role": "ADMINISTRATOR",
    "netId": "aggieJack"
};

var mockUser2 = {
    "lastName": "Daniels",
    "firstName": "Jill",
    "uin": "987654321",
    "exp": "1425393875282",
    "email": "aggieJill@library.tamu.edu",
    "role": "USER",
    "netId": "aggieJill"
};

var mockUser3 = {
    "lastName": "Smith",
    "firstName": "Jacob",
    "uin": "192837465",
    "exp": "1425393875282",
    "email": "jsmith@library.tamu.edu",
    "role": "USER",
    "netId": "jsmith"
};

angular.module('mock.user', []).
    service('User', function($q) {

      var self;

        return User;
});
