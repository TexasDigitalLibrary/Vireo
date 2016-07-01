vireo.service("SubmissionRepo", function($q, WsApi, VireoAbstractModel) {

	var SubmissionRepo = this;
	angular.extend(SubmissionRepo, VireoAbstractModel);

	var cache = {
		list : [],
		ready: false
	};

	var api = {
		request: {
			endpoint  : '/private/queue',
			controller: 'submission',
			method    : ''
		}
	};

	SubmissionRepo.getAll = function(sync) {
		cache.ready = sync ? !sync : cache.ready;
		SubmissionRepo.getAllPromise(api, cache);
		return cache.list;
	};

	SubmissionRepo.getCachePromise = function() {
		var defer = $q.defer();
		var promise = defer.promise();
		promise.then(function(){
			return SubmissionRepo.getAllPromise(api, cache);
		});
		return promise.resolve();
		// return $q.when(cache.list);
	};


	SubmissionRepo.getCachePromise = function() {
		return SubmissionRepo.getAllPromise(api, cache);
	};

	SubmissionRepo.findById = function(submissionId) {
		return WsApi.fetch(SubmissionRepo.buildRequest(api, 'get-one/' + submissionId))
			.then(function(rawResponse){
				return $q.when(JSON.parse(rawResponse.body).payload.Submission);
			});
	};

	SubmissionRepo.create = function(organizationId) {
		return WsApi.fetch(SubmissionRepo.buildRequest(api, 'create', {organizationId: organizationId}))
			.then(function(rawResponse){
				return $q.when(JSON.parse(rawResponse.body).payload.Submission.id);
			});
	};

	//We need access to the promise to refresh ng-table correctly,
	//so we must declare a modified version of the getAllPromise method here.
	SubmissionRepo.manualGetAllPromise = function() {
		if(cache.ready){
			return $q.resolve(cache.list);
		}
		
		var wsreq = WsApi.fetch(VireoAbstractModel.buildRequest(api, 'all')).then(function(response){
			var payload = angular.fromJson(response.body).payload;
			cache.list.length = 0;
			angular.forEach(Object.keys(payload), function(key){
				if (key.indexOf('ArrayList') > -1) {
					angular.extend(cache.list, payload[key]);
				} else {
					cache[key] = payload[key];
				}
			});
			cache.ready = true;
			return cache.list;
		});
		
		return wsreq;
	};

});
