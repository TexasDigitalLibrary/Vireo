vireo.service("UserSettings", function(AbstractModel, WsApi) {
	
	var self;

	var UserSettings = function(futureData) {
		self = this;
		angular.extend(self, AbstractModel);		
		self.unwrap(self, futureData);		
	};
	
	UserSettings.data = null;

	UserSettings.promise = null;
	
	UserSettings.set = function(data) {
		self.unwrap(self, data);
	};

	UserSettings.get = function() {
		
		// UserSettings.promise is made whether logged in or not!
		// Causing logging in to return the the cached data and not
		// getting the logged in users settings.
		if(UserSettings.promise && UserSettings.data.length > 2) {
			return UserSettings.data;
		}

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

   	UserSettings.reset = function(setting) {
   		UserSettings.data[setting] = UserSettings.data['_' + setting];
   	};

	UserSettings.update = function(setting, newValue) {
		WsApi.fetch({
				endpoint: '/private/queue', 
				controller: 'user', 
				method: 'settings/' + setting,
				data: { "settingValue": newValue }
		}).then(function(data) {
			
			var responseObject = JSON.parse(data.body);

			if(responseObject.meta.type == 'ERROR') {
				UserSettings.reset(setting);
			}
			else {
				UserSettings.data['_' + setting] = UserSettings.data[setting] = responseObject.payload.PersistentMap[setting];
			}
			
		}, function(data) {
			UserSettings.reset(setting);
		});
	};

	return UserSettings;
	
});
