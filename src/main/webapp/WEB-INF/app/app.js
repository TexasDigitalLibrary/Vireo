var vireo = angular.module('vireo', 
[
	'ngRoute',
	'ngSanitize',
	'ngCsv',
	'ngFileUpload',
	'vireo.version'
]);


vireo.repo = function(delegateName, delegateFunction) {
	console.log(delegateName.replace('Repo', ''))
	return vireo.factory(delegateName, delegateFunction).decorator(delegateName, function ($delegate, AbstractAppRepo) {
      	return angular.extend($delegate, new AbstractAppRepo($delegate.model, $delegate.mapping));
    });
};

vireo.model = function(delegateName, delegateFunction) {
	return vireo.factory(delegateName, function ($injector, AbstractModelNew, AbstractAppModel, api) {
		return function(data) {

			delegateFunction.$inject = $injector.annotate(delegateFunction);

			var modelConstructor = $injector.invoke(delegateFunction, delegateFunction.prototype);

			var abstractModel = new AbstractModelNew();
			var abstractAppModel = new AbstractAppModel();

			angular.extend(abstractAppModel, abstractModel);

			angular.extend(modelConstructor.prototype, abstractAppModel);

			var modelInstance = new modelConstructor();

			modelInstance.init(data, api[delegateName]);

			angular.extend(abstractAppModel, modelInstance);

			angular.extend(abstractModel, abstractAppModel);

			return modelInstance;
		};
	});
};

//This method's callback is passed to stomp and executed on both successfull connection, as well as disconnect.
setUpApp(function(connected) {

	//Indicates at app start if the app has successfully conenected to the service
	appConfig.connected = connected;

	vireo.constant('appConfig', appConfig);
	vireo.constant('api', apiMapping);

	angular.element(document).ready(function() {	   	
	   	try {
	   		// If the app is already bootstrapped then an error will be thrown
	   		// caution: if module is not found app will result in blank page with no stack trace!!!
			angular.bootstrap(document, ['core', 'vireo', 'ui.tinymce', 'ngSanitize', 'ngMessages', 'ngFileUpload', 'as.sortable', 'ui.bootstrap']);
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
