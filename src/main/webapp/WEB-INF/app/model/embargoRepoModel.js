vireo.service("EmbargoRepo", function(AbstractModel, WsApi, AlertService) {
	
	var self;

	var EmbargoRepo = function(futureData) {
		self = this;
		
		//This causes our model to extend AbstractModel
		angular.extend(self, AbstractModel);
		
		self.unwrap(self, futureData);			
	};
	
	EmbargoRepo.data = null;
	
	EmbargoRepo.listener = null;

	EmbargoRepo.promise = null;

	EmbargoRepo.set = function(data) {
		self.unwrap(self, data);		
	};

	EmbargoRepo.get = function() {

		if(EmbargoRepo.promise) return EmbargoRepo.data;
		
		var newAllEmbargoRepoPromise = WsApi.fetch({
			endpoint: '/private/queue', 
			controller: 'settings/embargo', 
			method: 'all',
		});

		EmbargoRepo.promise = newAllEmbargoRepoPromise;
		if (EmbargoRepo.data) {
			newAllEmbargoRepoPromise.then(function(data) {
				EmbargoRepo.set(JSON.parse(data.body).payload.HashMap);
			});
		} else {
			EmbargoRepo.data = new EmbargoRepo(newAllEmbargoRepoPromise);
		}

		EmbargoRepo.listener = WsApi.listen({
			endpoint: '/channel', 
			controller: 'settings/embargo', 
			method: '',
		});

		EmbargoRepo.listener.then(function(data) {
			debugger;
		});
				
		EmbargoRepo.set(EmbargoRepo.listener);

		return EmbargoRepo.data;

	};

	EmbargoRepo.create = function(embargo) {
		return WsApi.fetch({
			endpoint:'/private/queue',
			controller:'settings/embargo',
			method:'create',
			data: embargo
		});		

	};

	EmbargoRepo.update = function(embargo) {
		return WsApi.fetch({
			endpoint:'/private/queue',
			controller:'settings/embargo',
			method:'update',
			data: embargo
		});
	};
	
	EmbargoRepo.remove = function(id) {
        return WsApi.fetch({
            'endpoint': '/private/queue', 
            'controller': 'settings/embargo', 
            'method': 'remove/' + id
        });
    };
	
	EmbargoRepo.reorder = function(guarantor, src, dest) {
		return WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'settings/embargo', 
			'method': 'reorder/' + guarantor + '/' + src + '/' + dest
		});
	};
	
	EmbargoRepo.sort = function(guarantor, column) {
		return WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'settings/embargo', 
			'method': 'sort/' + guarantor + '/' + column
		});
	};

	EmbargoRepo.ready = function() {
		return EmbargoRepo.promise;
	};

	EmbargoRepo.listen = function() {
		return EmbargoRepo.listener;
	};

	return EmbargoRepo;
});