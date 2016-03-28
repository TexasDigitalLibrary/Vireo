angular.module('mock.wsApi', []).
    service('WsApi', function($q) {

        var WsApi = this;

        WsApi.fetch = function(apiReq) {
        	
        	var defer = $q.defer();
        	
        	switch(apiReq.controller) {
        		case 'admin': {
        			switch(apiReq.method) {
        				default: {

        				}; break;
        			}
        		} break;
        		case 'user': {
        			switch(apiReq.method) {
        				case 'credentials': defer.resolve({'content':mockUser1}); break;
        				case 'all': defer.resolve({'content':mockUserRepo1}); break;
        				case 'get': defer.resolve({'content':mockUser1}); break;
        				case 'update_role': {	        		
							mockUserRepo1['HashMap'][2].role = JSON.parse(apiReq['data']).role;
							defer.resolve(mockUserRepo1);
						}; break;
        				default: {
        					
        				}; break;
        			}
        		}; break;
                case 'settings/languages': {
                    switch(apiReq.method) {
                        case 'all': defer.resolve({'content':mockLanguageRepo1}); break;
                        default: {
                            
                        }; break;
                    }
                }; break;
                case 'settings/controlled-vocabulary': {
                    switch(apiReq.method) {
                        case 'all': defer.resolve({'content':mockControlledVocabularyRepo1}); break;
                        default: {
                            
                        }; break;
                    }
                }; break;
                case 'settings/deposit-location': {
                    switch(apiReq.method) {
                        case 'all': defer.resolve({'content':mockDepositLocationRepo1}); break;
                        default: {
                            
                        }; break;
                    }
                } break;
        		default: {
        			switch(apiReq.method) {
        				default: {
        					
        				}; break;
        			}
        		}; break;
        	}
        	            
            return defer.promise;
        }
        
        WsApi.listen = function(apiReq) {        	
        	var defer = $q.defer();
            return defer.promise;
        }
            
});