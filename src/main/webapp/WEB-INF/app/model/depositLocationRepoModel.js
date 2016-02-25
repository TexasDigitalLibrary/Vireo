vireo.service("DepositLocationRepo", function(WsApi, AbstractModel, AlertService) {

	var self;
	
	var DepositLocationRepo = function(futureData) {
		self = this;

		//This causes our model to extend AbstractModel
		angular.extend(self, AbstractModel);
		
		self.unwrap(self, futureData);		
	};
	
	DepositLocationRepo.data = null;
	
	DepositLocationRepo.listener = null;

	DepositLocationRepo.promise = null;
	
	DepositLocationRepo.set = function(data) {
		self.unwrap(self, data);
	};

	DepositLocationRepo.get = function() {

		if(DepositLocationRepo.promise) return DepositLocationRepo.data;

		var newDepositLocationRepoPromise = WsApi.fetch({
			endpoint: '/private/queue', 
			controller: 'settings/deposit-location', 
			method: 'all',
		});

		DepositLocationRepo.promise = newDepositLocationRepoPromise;

		if(DepositLocationRepo.data) {
			newDepositLocationRepoPromise.then(function(data) {
				DepositLocationRepo.set(JSON.parse(data.body).payload.HashMap);
			});
		}
		else {
			DepositLocationRepo.data = new DepositLocationRepo(newDepositLocationRepoPromise);	
		}

		DepositLocationRepo.listener = WsApi.listen({
			endpoint: '/channel', 
			controller: 'settings/deposit-location', 
			method: '',
		});
				
		DepositLocationRepo.set(DepositLocationRepo.listener);

		return DepositLocationRepo.data;	
	};

	DepositLocationRepo.add = function(depositLocation) {
		WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'settings/deposit-location', 
			'method': 'create',
			'data': depositLocation
		}).then(function(response) {
			var responseType = angular.fromJson(response.body).meta.type;
			var responseMessage = angular.fromJson(response.body).meta.message;
			if(responseType != 'SUCCESS') {
				AlertService.add({type: responseType, message: responseMessage}, "/settings/deposit-location");  
			}
		});
	};

	DepositLocationRepo.update = function(depositLocation) {
		console.log(depositLocation);
		// WsApi.fetch({
		// 	'endpoint': '/private/queue', 
		// 	'controller': 'settings/deposit-location', 
		// 	'method': 'create',
		// 	'data': depositLocation
		// }).then(function(response) {
		// 	var responseType = angular.fromJson(response.body).meta.type;
		// 	var responseMessage = angular.fromJson(response.body).meta.message;
		// 	if(responseType != 'SUCCESS') {
		// 		AlertService.add({type: responseType, message: responseMessage}, "/settings/deposit-location");  
		// 	}
		// });
	};

	DepositLocationRepo.reorder = function(src, dest) {
		WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'settings/deposit-location', 
			'method': 'reorder/' + src + '/' + dest
		}).then(function(response) {
			var responseType = angular.fromJson(response.body).meta.type;
			var responseMessage = angular.fromJson(response.body).meta.message;
			if(responseType != 'SUCCESS') {
				AlertService.add({type: responseType, message: responseMessage}, "/settings/deposit-location");  
			}
		});
	};

	DepositLocationRepo.remove = function(index) {
		WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'settings/deposit-location', 
			'method': 'remove/' + index
		}).then(function(response) {
			var responseType = angular.fromJson(response.body).meta.type;
			var responseMessage = angular.fromJson(response.body).meta.message;
			if(responseType != 'SUCCESS') {
				AlertService.add({type: responseType, message: responseMessage}, "/settings/deposit-location");  
			}
		});
	};
	
	DepositLocationRepo.ready = function() {
		return DepositLocationRepo.promise;
	};

	DepositLocationRepo.listen = function() {
		return DepositLocationRepo.listener;
	};
	
	return DepositLocationRepo;
	
});