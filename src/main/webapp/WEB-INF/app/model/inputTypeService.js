vireo.service("InputTypeService", function($q, WsApi) {

	var cache = {list:[], cached: false};

	//Return a promise of real data, and caches the real data upon fulfillment.
	this.getAllPromise = function() {
		if(cache.cached){
			return $q.resolve(cache.list).then(function(data){
				cache.list = data;
			});
		}

		return wsResult = WsApi.fetch({
			'endpoint'  : '/private/queue',
			'controller': 'settings/input-types',
			'method'    : 'all'
		}).then(function(data){
			cache.list.length = 0;
			cache.list.push(data);
			cache.cached = true;
		});
	}

	this.getAll = function(){
		this.getAllPromise();
		return cache.list;
	}

});
