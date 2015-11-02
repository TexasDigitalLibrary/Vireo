var seedApp = angular.module('seedApp', 
[
	'ngRoute',
	'seedApp.version'
]);

//This method's callback is passed to stomp and executed on both successfull connection, as well as disconnect.
setUpApp(function(connected) {

	//Indicates at app start if the app has successfully conenected to the service
	appConfig.connected = connected;

	seedApp.constant('appConfig', appConfig);

	angular.element(document).ready(function() {	   	
	   	try {
	   		// If the app is already bootstrapped then an error will be thrown
			angular.bootstrap(document, ['core', 'seedApp']);
		} catch (e) {
			/*
			 * If websockets dissconnect the app will attempt to re-bootstrap. Since the app is already running we will
			 * end up in this block, and can generate an error indicating the disconnect.
			 */
	    	var doc = angular.element(document);
	        var injector = doc.injector();
	        if(typeof injector != 'undefined') {
	        	AlertService = injector.get('AlertService');
	    		AlertService.add({type: "ERROR", message: "Web service cannot be reached."}, "/app/errors");
	        }
	    }
	});
		
});