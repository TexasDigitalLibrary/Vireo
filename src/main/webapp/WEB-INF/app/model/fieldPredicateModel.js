vireo.service("FieldPredicateModel", function($q, WsApi, VireoAbstractModel) {

	var FieldPredicateModel = this;
	angular.extend(FieldPredicateModel, VireoAbstractModel);

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

	FieldPredicateModel.getAll = function(sync){
		cache.ready = sync ? !sync : cache.ready;
		this.getAllPromise(api, cache);
		return cache.list;
	};

	FieldPredicateModel.addPredicate = function(predicate){
		return WsApi.fetch(FieldPredicateModel.buildRequest(api, 'create', predicate));
	};

	FieldPredicateModel.predicateWithValue = function(value) {
        return FieldPredicateModel.findBy(api, cache, 'value', value);
	};

});

