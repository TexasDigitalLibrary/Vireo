vireo.service("FieldGlossModel", function($q, WsApi, VireoAbstractModel) {

	var FieldGlossModel = this;
	angular.extend(FieldGlossModel, VireoAbstractModel);

	var cache = {
		list  : [],
		ready: false
	};

	var api = {
		request: {
			endpoint  : '/private/queue',
			controller: 'settings/field-gloss',
			method    : ''
		}
	};

	FieldGlossModel.getAll = function(sync){
		cache.ready = sync ? !sync : cache.ready;
		FieldGlossModel.getAllPromise(api, cache);
		return cache.list;
	};

	FieldGlossModel.addGloss = function(gloss){
		return WsApi.fetch(FieldGlossModel.buildRequest(api, 'create', gloss));
	};

	FieldGlossModel.glossWithValue = function(value) {
        return FieldGlossModel.findBy(api, cache, 'value', value);
	};

});
