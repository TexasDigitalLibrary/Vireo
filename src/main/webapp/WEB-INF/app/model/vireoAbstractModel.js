vireo.service("VireoAbstractModel", function($q, WsApi) {

	// Convenience function to create a WS API request dictionary
	// using a given API, method, and optional data.
	this.buildRequest = function(api, method, data) {
		console.info('in build request api: ', api, ' method ', method, ' data: ', data);
		var builtRequest = angular.copy(api.request);
		builtRequest.method = method;

		if (angular.isDefined(data)){
			builtRequest.data = data;
		}
		
		return builtRequest;
	};

	// Convenience function to abstract common getter logic.
	// Returns a promise of real data, and caches the real data upon fulfillment
	// using the given cache.
	this.getAllPromise = function(api, cache) {
		if(cache.ready){
			return $q.resolve(cache.list);
		}
		return WsApi.fetch(this.buildRequest(api, 'all')).then(function(response){
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
		});
	};

});
