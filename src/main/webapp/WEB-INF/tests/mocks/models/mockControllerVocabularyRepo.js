var mockControlledVocabularyRepo1 = {
    "list": [
        {
            "id": 1,
            "order": 1,
            "name": "guarantor",
            "entityName": "Embargo",
            "language": {
                "id": 1,
                "order": null,
                "name": "English"
            },
            "dictionary": [
                "DEFAULT",
                "PROQUEST"
            ],
            "enum": true,
            "entityProperty": true
        },
        {
            "id": 2,
            "order": 2,
            "name": "type",
            "entityName": "Attachment",
            "language": {
                "id": 1,
                "order": null,
                "name": "English"
            },
            "dictionary": [],
            "enum": false,
            "entityProperty": true
        },
        {
            "id": 3,
            "order": 3,
            "name": "test",
            "entityName": null,
            "language": {
                "id": 1,
                "order": null,
                "name": "English"
            },
            "dictionary": [],
            "enum": false,
            "entityProperty": false
        }
    ]
};

var mockControlledVocabularyRepo2 = {
    "list": [
        {
            "id": 1,
            "order": 1,
            "name": "reviewer",
            "entityName": null,
            "language": {
                "id": 1,
                "order": null,
                "name": "English"
            },
            "dictionary": [],
            "enum": true,
            "entityProperty": true
        },
        {
            "id": 2,
            "order": 2,
            "name": "type",
            "entityName": "Attachment",
            "language": {
                "id": 1,
                "order": null,
                "name": "English"
            },
            "dictionary": [],
            "enum": false,
            "entityProperty": true
        },
        {
            "id": 3,
            "order": 3,
            "name": "test",
            "entityName": null,
            "language": {
                "id": 1,
                "order": null,
                "name": "English"
            },
            "dictionary": [],
            "enum": false,
            "entityProperty": false
        }
    ]
};

var mockControlledVocabularyRepo3 = {
    "list": [
        {
            "id": 1,
            "order": 1,
            "name": "guarantor",
            "entityName": "Embargo",
            "language": {
                "id": 1,
                "order": null,
                "name": "English"
            },
            "dictionary": [
                "DEFAULT",
                "PROQUEST"
            ],
            "enum": true,
            "entityProperty": true
        },
        {
            "id": 2,
            "order": 2,
            "name": "type",
            "entityName": "Attachment",
            "language": {
                "id": 1,
                "order": null,
                "name": "English"
            },
            "dictionary": [],
            "enum": false,
            "entityProperty": true
        },
        {
            "id": 3,
            "order": 3,
            "name": "subjects",
            "entityName": null,
            "language": {
                "id": 1,
                "order": null,
                "name": "English"
            },
            "dictionary": [],
            "enum": false,
            "entityProperty": false
        }
    ]
};

angular.module('mock.controlledVocabularyRepo', []).
    service('ControlledVocabularyRepo', function($q) {
    	
    	var self;
    	
    	var ControlledVocabularyRepo = function(futureData) {
    		self = this;
			
    		if(!futureData.$$state) {
    			angular.extend(self, futureData);
    			return;
    		}

    		futureData.then(null, null, function(data) {
    			angular.extend(self, data);	
    		});

    	}
    	
    	ControlledVocabularyRepo.get = function() {
            return new ControlledVocabularyRepo(mockControlledVocabularyRepo1);
        };
        
        ControlledVocabularyRepo.set = function(languages) {
        	angular.extend(self, languages);
        };

        ControlledVocabularyRepo.downloadCSV = function(controlledVocabulary) {
			
		};

		ControlledVocabularyRepo.uploadCSV = function(controlledVocabulary) {
			
		};

		ControlledVocabularyRepo.confirmCSV = function(file, controlledVocabulary) {
			
		};

		ControlledVocabularyRepo.cancel = function(controlledVocabulary) {
			
		};

		ControlledVocabularyRepo.status = function(controlledVocabulary) {
			
		};

		ControlledVocabularyRepo.add = function(controlledVocabulary) {
			
		};

		ControlledVocabularyRepo.update = function(controlledVocabulary) {
			
		};

		ControlledVocabularyRepo.reorder = function(src, dest) {
			
		};

		ControlledVocabularyRepo.sort = function(column) {
			
		};

		ControlledVocabularyRepo.remove = function(index) {
			
		};        

        ControlledVocabularyRepo.ready = function() {
        	
        }; 
        
        ControlledVocabularyRepo.listen = function() {
        	return $q(function(resolve) {            	
            	resolve(mockControlledVocabularyRepo3);
            });
        }; 

        ControlledVocabularyRepo.listenForChange = function() {
        	
        }; 
        
        return ControlledVocabularyRepo;
});