vireo.service("FieldPredicateModel", function($q, WsApi, VireoAbstractModel) {

	var cache = {
		list  : [],
		ready: false
	};

	var api = {
		request: {
			endpoint  : '/private/queue',
			controller: 'settings/field-predicates',
			method    : ''
		}
	};

	this.getAll = function(sync){
		cache.ready = sync ? !sync : cache.ready;
		VireoAbstractModel.getAllPromise(api, cache);
		return cache.list;
	};

	this.addPredicate = function(predicate){
		return WsApi.fetch(VireoAbstractModel.buildRequest(api, 'create', predicate));
	};

	this.predicateWithValue = function(value) {
        return VireoAbstractModel.findBy(api, cache, 'value', value);
	};

});

