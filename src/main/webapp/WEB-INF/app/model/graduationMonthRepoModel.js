vireo.service("GraduationMonthRepo", function(WsApi, AbstractModel, AlertService) {

	var self;
	
	var GraduationMonthRepo = function(futureData) {
		self = this;

		//This causes our model to extend AbstractModel
		angular.extend(self, AbstractModel);
		
		self.unwrap(self, futureData);		
	};
	
	GraduationMonthRepo.data = null;
	
	GraduationMonthRepo.listener = null;

	GraduationMonthRepo.promise = null;
	
	GraduationMonthRepo.set = function(data) {
		self.unwrap(self, data);
	};

	GraduationMonthRepo.get = function() {

		if(GraduationMonthRepo.promise) return GraduationMonthRepo.data;

		var newGraduationMonthRepoPromise = WsApi.fetch({
			endpoint: '/private/queue', 
			controller: 'settings/graduation-month', 
			method: 'all',
		});

		GraduationMonthRepo.promise = newGraduationMonthRepoPromise;

		if(GraduationMonthRepo.data) {
			newGraduationMonthRepoPromise.then(function(data) {
				GraduationMonthRepo.set(JSON.parse(data.body).payload.HashMap);
			});
		}
		else {
			GraduationMonthRepo.data = new GraduationMonthRepo(newGraduationMonthRepoPromise);	
		}

		GraduationMonthRepo.listener = WsApi.listen({
			endpoint: '/channel', 
			controller: 'settings/graduation-month', 
			method: '',
		});
				
		GraduationMonthRepo.set(GraduationMonthRepo.listener);

		return GraduationMonthRepo.data;	
	};

	GraduationMonthRepo.add = function(depositLocation) {
		return WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'settings/graduation-month', 
			'method': 'create',
			'data': depositLocation
		}).then(function(response) {
			var responseType = angular.fromJson(response.body).meta.type;
			var responseMessage = angular.fromJson(response.body).meta.message;
			if(responseType != 'SUCCESS') {
				AlertService.add({type: responseType, message: responseMessage}, "/settings/graduation-month");  
			}
		});
	};

	GraduationMonthRepo.update = function(depositLocation) {
		return WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'settings/graduation-month', 
			'method': 'update',
			'data': depositLocation
		}).then(function(response) {
			var responseType = angular.fromJson(response.body).meta.type;
			var responseMessage = angular.fromJson(response.body).meta.message;
			if(responseType != 'SUCCESS') {
				AlertService.add({type: responseType, message: responseMessage}, "/settings/graduation-month");  
			}
		});
	};

	GraduationMonthRepo.reorder = function(src, dest) {
		return WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'settings/graduation-month', 
			'method': 'reorder/' + src + '/' + dest
		}).then(function(response) {
			var responseType = angular.fromJson(response.body).meta.type;
			var responseMessage = angular.fromJson(response.body).meta.message;
			if(responseType != 'SUCCESS') {
				AlertService.add({type: responseType, message: responseMessage}, "/settings/graduation-month");  
			}
		});
	};

	GraduationMonthRepo.sort = function(column) {
		return WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'settings/graduation-month', 
			'method': 'sort/' + column
		}).then(function(response) {
			var responseType = angular.fromJson(response.body).meta.type;
			var responseMessage = angular.fromJson(response.body).meta.message;
			if(responseType != 'SUCCESS') {
				AlertService.add({type: responseType, message: responseMessage}, "/settings/graduation-month");  
			}
		});
	};

	GraduationMonthRepo.remove = function(index) {
		return WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'settings/graduation-month', 
			'method': 'remove/' + index
		}).then(function(response) {
			var responseType = angular.fromJson(response.body).meta.type;
			var responseMessage = angular.fromJson(response.body).meta.message;
			if(responseType != 'SUCCESS') {
				AlertService.add({type: responseType, message: responseMessage}, "/settings/graduation-month");  
			}
		});
	};
	
	GraduationMonthRepo.ready = function() {
		return GraduationMonthRepo.promise;
	};

	GraduationMonthRepo.listen = function() {
		return GraduationMonthRepo.listener;
	};
	
	return GraduationMonthRepo;
	
});
