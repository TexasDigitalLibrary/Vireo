vireo.service("InputTypeService", function($q, $timeout, WsApi) {

	var cache = {inputTypesCache:[], cached: false}; //May be either {}, a promise, or a real payload object at any time.

	//Return a promise of real data, and caches the real data upon fulfillment.
	this.getAll = function() {
		if(cache.cached){
			return $q.resolve(cache.inputTypesCache).then(function(data){
				console.info('callback cache fired; mapping data', data);
				cache.inputTypesCache = data;
			});
		}

		return wsResult = WsApi.fetch({
			'endpoint'  : '/private/queue',
			'controller': 'settings/input-types',
			'method'    : 'all'
		}).then(function(data){
			console.info('ws callback fired; mapping data', data);
			cache.inputTypesCache.length = 0;
			cache.inputTypesCache.push(data);
			cache.cached = true;
		});
	}

	this.inputTypes = function(){
		this.getAll();
		return cache.inputTypesCache;
	}

});
