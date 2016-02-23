var mockDepositLocationRepo1 = {
	'HashMap':{
		'list':[
            {
                "id": 1,
                "order": 1,
                "name": "Test0",
                "repository": "Dspace",
                "collection": null,
                "username": "test@tdl.org",
                "password": "abc123",
                "onBehalfOf": "TDL",
                "packager": "VireoExport",
                "depositor": "Sword1Deposit",
                "timeout": 100
            },
            {
                "id": 2,
                "order": 2,
                "name": "Test1",
                "repository": "Fedora",
                "collection": null,
                "username": "test@tdl.org",
                "password": "abc123",
                "onBehalfOf": "Texas A&M",
                "packager": "VireoExport",
                "depositor": "Sword1Deposit",
                "timeout": 200
            },
            {
                "id": 3,
                "order": 3,
                "name": "Test2",
                "repository": "Nuxio",
                "collection": null,
                "username": "test@tdl.org",
                "password": "abc123",
                "onBehalfOf": "Texas A&M",
                "packager": "VireoExport",
                "depositor": "FileDeposit",
                "timeout": 300
            }
        ]
	}
};

var mockDepositLocationRepo2 = {
    'HashMap':{
        'list':[
            {
                "id": 1,
                "order": 1,
                "name": "Test3",
                "repository": "Dspace",
                "collection": null,
                "username": "test@tdl.org",
                "password": "abc123",
                "onBehalfOf": "TDL",
                "packager": "VireoExport",
                "depositor": "Sword1Deposit",
                "timeout": 100
            },
            {
                "id": 2,
                "order": 2,
                "name": "Test4",
                "repository": "Fedora",
                "collection": null,
                "username": "test@tdl.org",
                "password": "abc123",
                "onBehalfOf": "Texas A&M",
                "packager": "VireoExport",
                "depositor": "Sword1Deposit",
                "timeout": 200
            },
            {
                "id": 3,
                "order": 3,
                "name": "Test5",
                "repository": "Nuxio",
                "collection": null,
                "username": "test@tdl.org",
                "password": "abc123",
                "onBehalfOf": "Texas A&M",
                "packager": "VireoExport",
                "depositor": "FileDeposit",
                "timeout": 300
            }
        ]
    }
};

var mockDepositLocationRepo3 = {
    'HashMap':{
        'list':[
            {
                "id": 1,
                "order": 1,
                "name": "Test3",
                "repository": "Dspace",
                "collection": null,
                "username": "test@tdl.org",
                "password": "abc123",
                "onBehalfOf": "TDL",
                "packager": "VireoExport",
                "depositor": "Sword1Deposit",
                "timeout": 100
            },
            {
                "id": 2,
                "order": 2,
                "name": "Test2",
                "repository": "Fedora",
                "collection": null,
                "username": "test@tdl.org",
                "password": "abc123",
                "onBehalfOf": "Texas A&M",
                "packager": "VireoExport",
                "depositor": "Sword1Deposit",
                "timeout": 200
            },
            {
                "id": 3,
                "order": 3,
                "name": "Test1",
                "repository": "Nuxio",
                "collection": null,
                "username": "test@tdl.org",
                "password": "abc123",
                "onBehalfOf": "Texas A&M",
                "packager": "VireoExport",
                "depositor": "FileDeposit",
                "timeout": 300
            }
        ]
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
            
        };
        
        DepositLocationRepo.reorder = function() {
            
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