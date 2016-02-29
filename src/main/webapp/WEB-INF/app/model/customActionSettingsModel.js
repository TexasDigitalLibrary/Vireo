vireo.service("CustomActionSettings", function(AbstractModel, WsApi, AlertService) {
	var self;

	var CustomActionSettings = function(futureData) {
		self = this;
		angular.extend(self, AbstractModel);		
		self.unwrap(self, futureData,"PersistentMap");			
	};
	
	CustomActionSettings.data = null;
	
	CustomActionSettings.listener = null;

	CustomActionSettings.promise = null;

	CustomActionSettings.set = function(data) {
		self.unwrap(self, data, "PersistentMap");		
	};
	

	CustomActionSettings.get = function() {

		if(CustomActionSettings.promise) return CustomActionSettings.data;
		
		var newAllCustomActionSettingsPromise = WsApi.fetch({
			endpoint: '/private/queue', 
			controller: 'settings/custom-action', 
			method: 'all'
		});

		CustomActionSettings.promise = newAllCustomActionSettingsPromise;
		if (CustomActionSettings.data) {
			newAllCustomActionSettingsPromise.then(function(data) {
				CustomActionSettings.set(JSON.parse(data.body).payload.HashMap);
			});
		} else {
			CustomActionSettings.data = new CustomActionSettings(newAllCustomActionSettingsPromise);
		}

		CustomActionSettings.listener = WsApi.listen({
			endpoint: '/channel', 
			controller: 'settings/custom-actions', 
			method: '',
		});

		CustomActionSettings.listener.then(function(data) {
			console.log(data);
		});
				
		CustomActionSettings.set(CustomActionSettings.listener);

		return CustomActionSettings.data;

	};

	CustomActionSettings.create = function(customAction) {
		WsApi.fetch({
			endpoint:'/private/queue',
			controller:'settings/custom-action',
			method:'create',
			data: customAction
		}).then(function(response) {
			console.log(response);
			console.log(JSON.parse(response.body).payload);
			return response;
		});		

	};

	CustomActionSettings.update = function(customAction) {
		WsApi.fetch({
			endpoint:'/private/queue',
			controller:'settings/custom-action',
			method:'update',
			data: customAction
		}).then(function(response) {
			console.log(response);
			console.log(JSON.parse(response.body).payload);
			return response;
		});
	};
	
	CustomActionSettings.reorder = function(src, dest) {
		WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'settings/custom-action', 
			'method': 'reorder/' + src + '/' + dest
		}).then(function(response) {
			var responseType = angular.fromJson(response.body).meta.type;
			var responseMessage = angular.fromJson(response.body).meta.message;
			if(responseType != 'SUCCESS') {
				AlertService.add({type: responseType, message: responseMessage}, "/settings/custom-action");  
			}
		});
	};
	
	CustomActionSettings.remove = function(index) {
		WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'settings/custom-action', 
			'method': 'remove/' + index
		}).then(function(response) {
			var responseType = angular.fromJson(response.body).meta.type;
			var responseMessage = angular.fromJson(response.body).meta.message;
			if(responseType != 'SUCCESS') {
				AlertService.add({type: responseType, message: responseMessage}, "/settings/custom-action");  
			}
		});
	};

//	CustomActionSettings.reset = function(type,setting) {
//		WsApi.fetch({
//			endpoint:'/private/queue',
//			controller:'settings/configurable',
//			method:'reset',
//			data: {'type':type, 'setting':setting}
//		});
//	};

	CustomActionSettings.ready = function() {
		return CustomActionSettings.promise;
	};

	CustomActionSettings.listen = function() {
		return CustomActionSettings.listener;
	};

	return CustomActionSettings;
});