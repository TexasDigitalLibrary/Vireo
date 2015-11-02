var mockAssumedControl1 = {
	'user': {
		"uin": "123456789",
	    "lastName": "Daniels",
	    "firstName": "Jack",
	    "role": "ROLE_ADMIN"
	},
	'netid': '',
	'button': 'Unassume',
	'status': ''
};

var mockAssumedControl2 = {
	'user': {
		"uin": "987654321",
		"lastName": "Daniels",
	    "firstName": "Jill",
	    "role": "ROLE_USER"
	},
	'netid': '',
	'button': 'Unassume',
	'status': ''
};

var mockAssumedControl3 = {
	'user': {},
	'netid': '',
	'button': 'Assume',
	'status': ''
};

angular.module('mock.AssumedControl', []).
    service('AssumedControl', function($q) {
    	
    	var self;
    	
    	var AssumedControl = function(futureData) {
    		self = this;
			
    		if(!futureData.$$state) {
    			angular.extend(self, futureData);
    			return;
    		}

    		futureData.then(null, null, function(data) {
    			angular.extend(self, data);	
    		});

    	}
    	
    	AssumedControl.get = function() {
            return new AssumedControl(mockAssumedControl1);
        };
        
        AssumedControl.set = function(data) {
        	angular.extend(self, data);
        };
        
        AssumedControl.fetch = function() {
        	return $q(function(resolve) {            	
            	resolve(mockAssumedControl3);
            });
        }; 
        
        return AssumedControl;
});
