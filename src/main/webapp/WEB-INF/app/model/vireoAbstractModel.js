vireo.service("VireoAbstractModel", function($q, WsApi) {

	var VireoAbstractModel = this;

	// Convenience function to create a WS API request dictionary
	// using a given API, method, and optional data.
	VireoAbstractModel.buildRequest = function(api, method, data) {
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
	VireoAbstractModel.getAllPromise = function(api, cache) {
		if(cache.ready){
			return $q.resolve(cache.list);
		}
		return WsApi.fetch(VireoAbstractModel.buildRequest(api, 'all')).then(function(response){
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

	// Search a repo by attribute name and attribute value
	VireoAbstractModel.findBy = function(api, cache, propertyName, propertyValue) {
		var retVal = null;

		if (!cache.ready) { //If for this function is called before InputTypeService.getAll(), our cache would be empty.
			VireoAbstractModel.getAllPromise(api, cache).then(function(){ //Now we can be sure the cache is full. Proceed with evaluation.
				angular.forEach(cache.list, function(entityInCache){
					if (propertyValue == entityInCache[propertyName]) {
						retVal = entityInCache[propertyName];
					}
				});
			});
		}else{ //Cache is available. Evaluate right away.
			angular.forEach(cache.list, function(entityInCache){
				if (propertyValue == entityInCache[propertyName]) {
					retVal = entityInCache[propertyName];
				}
			});
		}
		return retVal;
	};

});
