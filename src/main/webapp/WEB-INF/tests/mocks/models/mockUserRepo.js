var mockUserRepo1 = [
    {
        "uin": "123456789",
        "lastName": "Daniels",
        "firstName": "Jack",
        "role": "ROLE_ADMIN"
    },
    {
        "uin": "987654321",
        "lastName": "Daniels",
        "firstName": "Jill",
        "role": "USER"
    },
    {
        "uin": "192837465",
        "lastName": "Smith",
        "firstName": "Jacob",
        "role": "USER"
    }
];

var mockUserRepo2 = [
    {
        "uin": "321654987",
        "lastName": "Daniels",
        "firstName": "John",
        "role": "ROLE_ADMIN"
    },
    {
        "uin": "789456123",
        "lastName": "Daniels",
        "firstName": "Joann",
        "role": "USER"
    },
    {
        "uin": "564738291",
        "lastName": "Smith",
        "firstName": "Joseph",
        "role": "USER"
    }
];

var mockUserRepo3 = [
    {
        "uin": "111111111",
        "lastName": "User1",
        "firstName": "Test",
        "role": "ROLE_ADMIN"
    },
    {
        "uin": "222222222",
        "lastName": "User2",
        "firstName": "Test",
        "role": "USER"
    },
    {
        "uin": "333333333",
        "lastName": "User3",
        "firstName": "Test",
        "role": "USER"
    }
];


angular.module('mock.userRepo', []).
    service('UserRepo', function($q) {
        
        var self;
        
        
        return UserRepo;
});