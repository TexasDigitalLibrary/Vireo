vireo.service("AvailableDocumentTypesRepo", function(WsApi, AbstractModel, AlertService) {

	var self;
	
	var AvailableDocumentTypesRepo = function(futureData) {
		self = this;

		//This causes our model to extend AbstractModel
		angular.extend(self, AbstractModel);
		
		self.unwrap(self, futureData);		
	};
	
	AvailableDocumentTypesRepo.data = null;
	
	AvailableDocumentTypesRepo.listener = null;

	AvailableDocumentTypesRepo.promise = null;
	
	AvailableDocumentTypesRepo.set = function(data) {
		self.unwrap(self, data);
	};

	AvailableDocumentTypesRepo.get = function() {

		if(AvailableDocumentTypesRepo.promise) return AvailableDocumentTypesRepo.data;

		var newAvailableDocumentTypesRepoPromise = WsApi.fetch({
			endpoint: '/private/queue', 
			controller: 'settings/document-types', 
			method: 'all',
		});
          newAvailableDocumentTypesRepoPromise.then(function(res){
            console.info('model promise');
            console.info(res);
          })

		AvailableDocumentTypesRepo.promise = newAvailableDocumentTypesRepoPromise;

		if(AvailableDocumentTypesRepo.data) {
			newAvailableDocumentTypesRepoPromise.then(function(data) {
				AvailableDocumentTypesRepo.set(JSON.parse(data.body).payload.HashMap);
			});
		}
		else {
			AvailableDocumentTypesRepo.data = new AvailableDocumentTypesRepo(newAvailableDocumentTypesRepoPromise);	
		}

		AvailableDocumentTypesRepo.listener = WsApi.listen({
			endpoint: '/channel', 
			controller: 'settings/document-types', 
			method: '',
		});
				
		AvailableDocumentTypesRepo.set(AvailableDocumentTypesRepo.listener);

		return AvailableDocumentTypesRepo.data;	
	};

        AvailableDocumentTypesRepo.add = function(documentType) {
          console.info('calling model creator');
          console.info(documentType);
		return WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'settings/document-types', 
			'method': 'create',
			'data': documentType
		});
	};

	AvailableDocumentTypesRepo.update = function(documentType) {
		return WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'settings/document-types', 
			'method': 'update',
			'data': documentType
		});
	};

	AvailableDocumentTypesRepo.reorder = function(src, dest) {
          console.info('calling reorder on server');
		return WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'settings/document-types', 
			'method': 'reorder/' + src + '/' + dest
		});
	};

	AvailableDocumentTypesRepo.sort = function(column) {
		return WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'settings/document-types', 
			'method': 'sort/' + column
		});
	};

	AvailableDocumentTypesRepo.remove = function(index) {
		return WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'settings/document-types', 
			'method': 'remove/' + index
		});
	};
	
	AvailableDocumentTypesRepo.ready = function() {
		return AvailableDocumentTypesRepo.promise;
	};

	AvailableDocumentTypesRepo.listen = function() {
		return AvailableDocumentTypesRepo.listener;
	};
	
	return AvailableDocumentTypesRepo;
	
});
