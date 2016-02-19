vireo.service("ConfigurableSettings", function(AbstractModel, WsApi) {
	var self;

	var ConfigurableSettings = function(futureData) {
		self = this;
		angular.extend(self, AbstractModel);		
		self.unwrap(self, futureData,"PersistentMap");			
	};
	
	ConfigurableSettings.data = null;

	ConfigurableSettings.promise = null;

	ConfigurableSettings.set = function(data) {
		self.unwrap(self, data, "PersistentMap");		
	};
	

	ConfigurableSettings.get = function() {
		if(ConfigurableSettings.promise) return ConfigurableSettings.data;
		
		var newAllConfigurableSettingsPromise = WsApi.fetch({
								endpoint: '/private/queue', 
								controller: 'settings', 
								method: 'all'
		});
		ConfigurableSettings.promise = newAllConfigurableSettingsPromise;
		ConfigurableSettings.data = new ConfigurableSettings(newAllConfigurableSettingsPromise);

		ConfigurableSettings.listener = WsApi.listen({
			endpoint: '/channel', 
			controller: 'settings', 
			method: '',
		});
				
		ConfigurableSettings.set(ConfigurableSettings.listener);

		return ConfigurableSettings.data;

	};

	ConfigurableSettings.update = function(type, setting, value) {		
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

	ConfigurableSettings.reset = function(type,setting) {
		WsApi.fetch({
			endpoint:'/private/queue',
			controller:'settings',
			method:'reset',
			data: {'type':type, 'setting':setting}
		});
	};

	ConfigurableSettings.ready = function() {
		return ConfigurableSettings.promise;
	};

	ConfigurableSettings.listen = function() {
		return ConfigurableSettings.listener;
	};

	return ConfigurableSettings;
});