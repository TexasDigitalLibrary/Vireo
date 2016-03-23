vireo.service("LanguageRepo", function(WsApi, AbstractModel, AlertService) {

	var self;
	
	var LanguageRepo = function(futureData) {
		self = this;

		//This causes our model to extend AbstractModel
		angular.extend(self, AbstractModel);
		
		self.unwrap(self, futureData);		
	};
	
	LanguageRepo.data = null;
	
	LanguageRepo.listener = null;

	LanguageRepo.promise = null;
	
	LanguageRepo.set = function(data) {
		self.unwrap(self, data);
	};

	LanguageRepo.get = function() {

		if(LanguageRepo.promise) return LanguageRepo.data;

		var newLanguageRepoPromise = WsApi.fetch({
			endpoint: '/private/queue', 
			controller: 'settings/languages', 
			method: 'all',
		});

		LanguageRepo.promise = newLanguageRepoPromise;

		if(LanguageRepo.data) {
			newLanguageRepoPromise.then(function(data) {
				LanguageRepo.set(JSON.parse(data.body).payload.HashMap);
			});
		}
		else {
			LanguageRepo.data = new LanguageRepo(newLanguageRepoPromise);	
		}

		LanguageRepo.listener = WsApi.listen({
			endpoint: '/channel', 
			controller: 'settings/languages', 
			method: '',
		});
				
		LanguageRepo.set(LanguageRepo.listener);

		return LanguageRepo.data;
	};

	LanguageRepo.add = function(language) {
		return WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'settings/languages', 
			'method': 'create',
			'data': language
		});
	};

	LanguageRepo.update = function(language) {
		return WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'settings/languages', 
			'method': 'update',
			'data': language
		});
	};

	LanguageRepo.reorder = function(src, dest) {
		return WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'settings/languages', 
			'method': 'reorder/' + src + '/' + dest
		});
	};

	LanguageRepo.sort = function(column) {
		return WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'settings/languages', 
			'method': 'sort/' + column
		});
	};

	LanguageRepo.remove = function(index) {
		console.log(index);
		return WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'settings/languages', 
			'method': 'remove/' + index
		});
	};

	LanguageRepo.ready = function() {
		return LanguageRepo.promise;
	};

	LanguageRepo.listen = function() {
		return LanguageRepo.listener;
	};
	
	return LanguageRepo;
	
});