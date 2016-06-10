vireo.service("FieldPredicateModel", function($q, WsApi) {

	var cache = {
		list  : [],
		ready: false
	};

	var api = {
		request: {
			endpoint  : '/private/queue',
			controller: 'settings/field-predicates',
			method    : 'all'
		},
		type: 'ArrayList<FieldPredicate>'
	};

	//Return a promise of real data, and caches the real data upon fulfillment.
	this.getAllPromise = function() {
		if(cache.ready){
			return $q.resolve(cache.list);
		}
		return WsApi.fetch(api.request).then(function(response){
			console.info('raw response: ', response);
			cache.list.length = 0;
			angular.extend(cache.list, angular.fromJson(response.body).payload[api.type]);
			cache.ready = true;
		});
	}

	this.getAll = function(sync){
		cache.ready = sync ? !sync : cache.ready;
		this.getAllPromise();
		return cache.list;
	}

});

