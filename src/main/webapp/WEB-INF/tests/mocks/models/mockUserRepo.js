var mockUserRepo1 = {
    'HashMap':{
        '0':{
            "uin": "123456789",
            "lastName": "Daniels",
            "firstName": "Jack",
            "role": "ROLE_ADMIN"
        },
        '1':{
            "uin": "987654321",
            "lastName": "Daniels",
            "firstName": "Jill",
            "role": "ROLE_USER"
        },
        '2':{
            "uin": "192837465",
            "lastName": "Smith",
            "firstName": "Jacob",
            "role": "ROLE_USER"
        }
    }
};

var mockUserRepo2 = {
    'HashMap':{
        '0':{
            "uin": "321654987",
            "lastName": "Daniels",
            "firstName": "John",
            "role": "ROLE_ADMIN"
        },
        '1':{
            "uin": "789456123",
            "lastName": "Daniels",
            "firstName": "Joann",
            "role": "ROLE_USER"
        },
        '2':{
            "uin": "564738291",
            "lastName": "Smith",
            "firstName": "Joseph",
            "role": "ROLE_USER"
        }
    }
};

var mockUserRepo3 = {
    'HashMap':{
        '0':{
            "uin": "111111111",
            "lastName": "User1",
            "firstName": "Test",
            "role": "ROLE_ADMIN"
        },
        '1':{
            "uin": "222222222",
            "lastName": "User2",
            "firstName": "Test",
            "role": "ROLE_USER"
        },
        '2':{
            "uin": "333333333",
            "lastName": "User3",
            "firstName": "Test",
            "role": "ROLE_USER"
        }
    }
};


angular.module('mock.userRepo', []).
    service('UserRepo', function($q) {
        
        var self;
        
        var UserRepo = function(futureData) {
            self = this;
            
            if(!futureData.$$state) {
                angular.extend(self, futureData);
                return;
            }

            futureData.then(null, null, function(data) {
                angular.extend(self, data); 
            });

        }
        
        UserRepo.get = function() {
            return new UserRepo(mockUserRepo1);
        };
        
        UserRepo.set = function(users) {
            angular.extend(self, users);
        };
        
        UserRepo.fetch = function() {
            return $q(function(resolve) {               
                resolve(mockUserRepo3);
            });
        }; 
        
        UserRepo.listen = function() {
            return $q(function(resolve) {               
                resolve(mockUserRepo3);
            });
        }; 
        
        return UserRepo;
});