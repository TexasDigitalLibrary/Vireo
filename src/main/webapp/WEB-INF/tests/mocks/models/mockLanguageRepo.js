var mockLanguageRepo1 = {
    "list": [
        {
            "id": 1,
            "position": null,
            "name": "English"
        },
        {
        	"id": 2,
        	"position": null,
        	"name": "Spanish"
        },
        {
        	"id": 3,
        	"position": null,
        	"name": "French"
        }
    ]
};

var mockLanguageRepo2 = {
    "list": [
        {
            "id": 1,
            "position": null,
            "name": "English"
        },
        {
        	"id": 2,
        	"position": null,
        	"name": "Chinese"
        },
        {
        	"id": 3,
        	"position": null,
        	"name": "French"
        }
    ]
};

var mockLanguageRepo3 = {
    "list": [
        {
            "id": 1,
            "position": null,
            "name": "English"
        },
        {
        	"id": 2,
        	"position": null,
        	"name": "Spanish"
        },
        {
        	"id": 3,
        	"position": null,
        	"name": "German"
        }
    ]
};

angular.module('mock.languageRepo', []).
    service('LanguageRepo', function($q) {
    	
    	var self;
    	
    	var LanguageRepo = function(futureData) {
    		self = this;
			
    		if(!futureData.$$state) {
    			angular.extend(self, futureData);
    			return;
    		}

    		futureData.then(null, null, function(data) {
    			angular.extend(self, data);	
    		});

    	}
    	
    	LanguageRepo.get = function() {
            return new LanguageRepo(mockLanguageRepo1);
        };
        
        LanguageRepo.set = function(languages) {
        	angular.extend(self, languages);
        };
        
        LanguageRepo.fetch = function() {
        	return $q(function(resolve) {            	
            	resolve(mockLanguageRepo3);
            });
        }; 
        
        LanguageRepo.listen = function() {
        	return $q(function(resolve) {            	
            	resolve(mockLanguageRepo3);
            });
        };

        LanguageRepo.ready = function() {
            return $q(function(resolve) {               
                resolve(mockLanguageRepo3);
            });
        };
        
        return LanguageRepo;
});