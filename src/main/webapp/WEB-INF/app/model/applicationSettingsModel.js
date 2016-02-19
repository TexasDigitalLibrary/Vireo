vireo.service("ApplicationSettings", function(AbstractModel, WsApi) {
	var self;

	var ApplicationSettings = function(futureData) {
		self = this;
		angular.extend(self, AbstractModel);		
		self.unwrap(self, futureData,"PersistentMap");			
	};
	
	ApplicationSettings.data = null;

	ApplicationSettings.promise = null;

	ApplicationSettings.set = function(data) {
		self.unwrap(self, data, "PersistentMap");		
	};
	

	ApplicationSettings.get = function() {
		if(ApplicationSettings.promise) return ApplicationSettings.data;
		
		var newAllApplicationSettingsPromise = WsApi.fetch({
								endpoint: '/private/queue', 
								controller: 'settings', 
								method: 'all'
		});
		ApplicationSettings.promise = newAllApplicationSettingsPromise;
		ApplicationSettings.data = new ApplicationSettings(newAllApplicationSettingsPromise);

		ApplicationSettings.listener = WsApi.listen({
			endpoint: '/channel', 
			controller: 'settings', 
			method: '',
		});
				
		ApplicationSettings.set(ApplicationSettings.listener);

		return ApplicationSettings.data;

	};

	ApplicationSettings.update = function(type, setting, value) {		
		WsApi.fetch({
			endpoint:'/private/queue',
			controller:'settings',
			method:'update',
			data: {'type':type, 'setting':setting,'value':value}
		}).then(function(response) {
			console.log(response);
			console.log(JSON.parse(response.body).payload);
		});

		console.log("update type and value ENDS");
	};

	ApplicationSettings.reset = function(type,setting) {
		WsApi.fetch({
			endpoint:'/private/queue',
			controller:'settings',
			method:'reset',
			data: {'type':type, 'setting':setting}
		});
	};

	ApplicationSettings.ready = function() {
		return ApplicationSettings.promise;
	};

	ApplicationSettings.listen = function() {
		return ApplicationSettings.listener;
	};

	return ApplicationSettings;
});