vireo.service("InputTypeService", function($q, WsApi) {

	var cache = {
		list  : [],
		cached: false
	};
	var api = {
		request: {
			endpoint  : '/private/queue',
			controller: 'settings/input-types',
			method    : 'all'
		},
		type: 'ArrayList<InputType>'
	}

	//Return a promise of real data, and caches the real data upon fulfillment.
	this.getAllPromise = function() {
		if(cache.cached){
			return $q.resolve(cache.list).then(function(data){
				cache.list = data;
			});
		}

		return wsResult = WsApi.fetch(api.request).then(function(response){
			cache.list.length = 0;
			angular.extend(cache.list, angular.fromJson(response.body).payload[api.type]);
			cache.cached = true;
		});
	}

	this.getAll = function(){
		this.getAllPromise();
		return cache.list;
	}

});
