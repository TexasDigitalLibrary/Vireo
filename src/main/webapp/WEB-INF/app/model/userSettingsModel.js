vireo.service("UserSettings", function(AbstractModel, WsApi) {

	var self;

	var UserSettings = function(futureData) {
		self = this;
		angular.extend(self, AbstractModel);		
		self.unwrap(self, futureData, "PersistentMap");		
	};
	
	UserSettings.data = null;

	UserSettings.promise = null;
	
	UserSettings.set = function(data) {
		self.unwrap(self, data, "PersistentMap");
	};

	UserSettings.get = function() {

		if(UserSettings.promise) return UserSettings.data;

		UserSettings.promise = WsApi.fetch({
				endpoint: '/private/queue', 
				controller: 'user', 
				method: 'settings',
		});

		if(UserSettings.data) {
			UserSettings.promise.then(function(data) {
				UserSettings.set(JSON.parse(data.body).payload.PersistentMap);
			});
		}
		else {
			UserSettings.data = new UserSettings(UserSettings.promise);	
		}

		return UserSettings.data;
	};

	UserSettings.ready = function() {
		return UserSettings.promise;
	};

	UserSettings.refresh = function() {
       UserSettings.promise = null;
       UserSettings.get();
   	};

	UserSettings.update = function(setting, value) {

		// WsApi.fetch({
		// 		endpoint: '/private/queue', 
		// 		controller: 'user', 
		// 		method: 'settings',
		// 		data: 
		// }).then(function(data) {
		// 	UserSettings.data.settings = JSON.parse(data.body).payload.PersistentMap;
		// });

	}

	return UserSettings;
	
});
