vireo.service("ControlledVocabularyRepo", function(AbstractModel, AlertService, RestApi, WsApi) {

	var self;
	
	var ControlledVocabularyRepo = function(futureData) {
		self = this;

		//This causes our model to extend AbstractModel
		angular.extend(self, AbstractModel);
		
		self.unwrap(self, futureData);		
	};
	
	ControlledVocabularyRepo.data = null;
	
	ControlledVocabularyRepo.listener = null;

	ControlledVocabularyRepo.promise = null;
	
	ControlledVocabularyRepo.set = function(data) {
		self.unwrap(self, data);
	};

	ControlledVocabularyRepo.get = function() {

		if(ControlledVocabularyRepo.promise) return ControlledVocabularyRepo.data;

		var newControlledVocabularyRepoPromise = WsApi.fetch({
			endpoint: '/private/queue', 
			controller: 'settings/controlled-vocabulary', 
			method: 'all',
		});

		ControlledVocabularyRepo.promise = newControlledVocabularyRepoPromise;

		if(ControlledVocabularyRepo.data) {
			newControlledVocabularyRepoPromise.then(function(data) {
				ControlledVocabularyRepo.set(JSON.parse(data.body).payload.HashMap);
			});
		}
		else {
			ControlledVocabularyRepo.data = new ControlledVocabularyRepo(newControlledVocabularyRepoPromise);	
		}

		ControlledVocabularyRepo.listener = WsApi.listen({
			endpoint: '/channel', 
			controller: 'settings/controlled-vocabulary', 
			method: '',
		});
				
		ControlledVocabularyRepo.set(ControlledVocabularyRepo.listener);

		return ControlledVocabularyRepo.data;
	};

	ControlledVocabularyRepo.downloadCSV = function(controlledVocabulary) {
		return WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'settings/controlled-vocabulary', 
			'method': 'export/' + controlledVocabulary
		});
	};

	ControlledVocabularyRepo.uploadCSV = function(controlledVocabulary) {
		return WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'settings/controlled-vocabulary', 
			'method': 'import/' + controlledVocabulary
		});
	};

	ControlledVocabularyRepo.confirmCSV = function(file, controlledVocabulary) {
		return RestApi.post({
			'endpoint': '', 
			'controller': 'settings/controlled-vocabulary', 
			'method': 'compare/' + controlledVocabulary,
			'file': file
		});
	};

	ControlledVocabularyRepo.add = function(controlledVocabulary) {
		return WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'settings/controlled-vocabulary', 
			'method': 'create',
			'data': controlledVocabulary
		});
	};

	ControlledVocabularyRepo.update = function(controlledVocabulary) {
		return WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'settings/controlled-vocabulary', 
			'method': 'update',
			'data': controlledVocabulary
		});
	};

	ControlledVocabularyRepo.reorder = function(src, dest) {
		return WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'settings/controlled-vocabulary', 
			'method': 'reorder/' + src + '/' + dest
		});
	};

	ControlledVocabularyRepo.sort = function(column) {
		return WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'settings/controlled-vocabulary', 
			'method': 'sort/' + column
		});
	};

	ControlledVocabularyRepo.remove = function(index) {
		return WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'settings/controlled-vocabulary', 
			'method': 'remove/' + index
		});
	};

	ControlledVocabularyRepo.ready = function() {
		return ControlledVocabularyRepo.promise;
	};

	ControlledVocabularyRepo.listen = function() {
		return ControlledVocabularyRepo.listener;
	};
	
	return ControlledVocabularyRepo;
	
});