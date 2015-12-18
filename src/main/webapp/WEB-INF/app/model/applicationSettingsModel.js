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
		return ApplicationSettings.data;

	};

	ApplicationSettings.update = function(type, value) {		
		console.log("update type and value");
	};

	ApplicationSettings.reset = function(setting) {
		console.log("reset type and value");
	};

	return ApplicationSettings;
});