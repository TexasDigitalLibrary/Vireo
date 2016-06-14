vireo.service("FieldGlossModel", function($q, WsApi) {

	var cache = {
		list  : [],
		ready: false
	};

	var api = {
		request: {
			endpoint  : '/private/queue',
			controller: 'settings/field-gloss',
			method    : 'all'
		}
	};

	//Return a promise of real data, and caches the real data upon fulfillment.
	this.getAllPromise = function() {
		if(cache.ready){
			return $q.resolve(cache.list);
		}
		return WsApi.fetch(api.request).then(function(response){
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

	this.getAll = function(sync){
		cache.ready = sync ? !sync : cache.ready;
		this.getAllPromise();
		return cache.list;
	};

	this.glossWithValueExists = function(value) {
		var retVal = false;
		if (!cache.ready) { //If for this function is called before InputTypeService.getAll(), our cache would be empty.
			this.getAllPromise().then(function(){ //Now we can be sure the cache is full. Proceed with evaluation.
				angular.forEach(cache.list, function(glossInCache){
					if (value == glossInCache.value) {
						retVal = true;
					}
				});
			});
		}else{ //Cache is available. Evaluate right away.
			angular.forEach(cache.list, function(glossInCache){
				if (value == glossInCache.value) {
					retVal = true;
				}
			});
		}
		return retVal;
	};

});
