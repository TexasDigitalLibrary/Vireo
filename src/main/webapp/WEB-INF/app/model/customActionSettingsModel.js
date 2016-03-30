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
				
		CustomActionSettings.set(CustomActionSettings.listener);

		return CustomActionSettings.data;

	};

	CustomActionSettings.create = function(customAction) {
		return WsApi.fetch({
			endpoint:'/private/queue',
			controller:'settings/custom-action',
			method:'create',
			data: customAction
		});		

	};

	CustomActionSettings.update = function(customAction) {
		return WsApi.fetch({
			endpoint:'/private/queue',
			controller:'settings/custom-action',
			method:'update',
			data: customAction
		});
	};
	
	CustomActionSettings.reorder = function(src, dest) {
		return WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'settings/custom-action', 
			'method': 'reorder/' + src + '/' + dest
		});
	};
	
	CustomActionSettings.remove = function(index) {
		return WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'settings/custom-action', 
			'method': 'remove/' + index
		});
	};

	CustomActionSettings.ready = function() {
		return CustomActionSettings.promise;
	};

	CustomActionSettings.listen = function() {
		return CustomActionSettings.listener;
	};

	return CustomActionSettings;
});