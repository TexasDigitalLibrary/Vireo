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
    	
    	var User = function(futureData) {
    		self = this;
			
    		if(!futureData.$$state) {
    			angular.extend(self, futureData);
    			return;
    		}

    		futureData.then(null, null, function(data) {
    			angular.extend(self, data);	
    		});

    	}
    	
    	User.get = function() {
            return new User(mockUser1);
        };
        
        User.set = function(credentials) {
        	angular.extend(self, credentials);
        };
        
        User.fetch = function() {
        	return $q(function(resolve) {            	
            	resolve(mockUser3);
            });
        }; 

        User.ready = function() {
            return $q(function(resolve) {               
                resolve(mockUser3);
            });
        };
        
        return User;
});