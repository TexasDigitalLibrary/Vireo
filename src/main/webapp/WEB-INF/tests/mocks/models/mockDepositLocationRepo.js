var mockDepositLocationRepo1 = {
	'HashMap':{
		'0':{
			
		},
		'1':{
			
		},
		'2':{
			
		}
	}
};

var mockDepositLocationRepo2 = {
	'HashMap':{
		'0':{
			
		},
		'1':{
			
		},
		'2':{
			
		}
	}
};

var mockDepositLocationRepo3 = {
	'HashMap':{
		'0':{
			
		},
		'1':{
			
		},
		'2':{
			
		}
	}
};


angular.module('mock.depositLocationRepo', []).
    service('DepositLocationRepo', function($q) {
    	
    	var self;
    	
    	var DepositLocationRepo = function(futureData) {
    		self = this;
			
    		if(!futureData.$$state) {
    			angular.extend(self, futureData);
    			return;
    		}

    		futureData.then(null, null, function(data) {
    			angular.extend(self, data);	
    		});

    	};
    	
    	DepositLocationRepo.add = function() {
            return new DepositLocationRepo(mockDepositLocationRepo1);
        };
        
        DepositLocationRepo.reorder = function() {
            return new DepositLocationRepo(mockDepositLocationRepo1);
        };
    	
    	DepositLocationRepo.get = function() {
            return new DepositLocationRepo(mockDepositLocationRepo1);
        };
        
        DepositLocationRepo.set = function(depositLocations) {
        	angular.extend(self, depositLocations);
        };
        
        DepositLocationRepo.fetch = function() {
        	return $q(function(resolve) {            	
            	resolve(mockDepositLocationRepo3);
            });
        }; 
        
        DepositLocationRepo.listen = function() {
        	return $q(function(resolve) {            	
            	resolve(mockDepositLocationRepo3);
            });
        }; 
        
        return DepositLocationRepo;
});