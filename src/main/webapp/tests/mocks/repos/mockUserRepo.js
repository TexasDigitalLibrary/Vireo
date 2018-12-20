var mockUserRepo1 = [
    {
        firstName: "Jack",
        lastName: "Daniels",
        role: "ROLE_ADMIN",
        uin: "123456789"
    },
    {
        firstName: "Jill",
        lastName: "Daniels",
        role: "ROLE_USER",
        uin: "987654321"
    },
    {
        firstName: "Jacob",
        lastName: "Smith",
        role: "ROLE_USER",
        uin: "192837465"
    }
];

var mockUserRepo2 = [
    {
        firstName: "John",
        lastName: "Daniels",
        role: "ROLE_ADMIN",
        uin: "321654987"
    },
    {
        firstName: "Joann",
        lastName: "Daniels",
        role: "ROLE_USER",
        uin: "789456123"
    },
    {
        firstName: "Joseph",
        lastName: "Smith",
        role: "ROLE_USER",
        uin: "564738291"
    }
];

var mockUserRepo3 = [
    {
        firstName: "Test",
        lastName: "User1",
        role: "ROLE_ADMIN",
        uin: "111111111"
    },
    {
        firstName: "Test",
        lastName: "User2",
        role: "ROLE_USER",
        uin: "222222222"
    },
    {
        firstName: "Test",
        lastName: "User3",
        role: "ROLE_USER",
        uin: "333333333"
    }
];

angular.module('mock.userRepo', []).service('UserRepo', function($q) {
    var repo = mockRepo('UserRepo', $q, mockUser, mockUserRepo1);

    repo.getAllByRole = function (roles) {
        var found;
        for (var i in repo.list) {
            if (roles.indexOf(repo.list[i].role) !== -1) {
                found = angular.copy(repo.list[i]);
                break;
            }
        }

        return found;
    };

    repo.getAssignableUsers = function (roles) {
        var payload = repo.fetch();
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    return repo;
});
