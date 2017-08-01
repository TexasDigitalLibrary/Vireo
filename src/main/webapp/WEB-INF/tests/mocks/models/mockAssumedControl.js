var mockAssumedControl1 = {
	'user': {
		"uin": "123456789",
	    "lastName": "Daniels",
	    "firstName": "Jack",
	    "role": "ADMINISTRATOR"
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
	    "role": "USER"
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
    	
        
        return AssumedControl;
});
