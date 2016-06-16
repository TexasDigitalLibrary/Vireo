vireo.service("FieldGlossModel", function($q, WsApi, VireoAbstractModel) {

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

	this.getAll = function(sync){
		cache.ready = sync ? !sync : cache.ready;
		VireoAbstractModel.getAllPromise(api, cache);
		return cache.list;
	};

	this.addGloss = function(gloss){
		return WsApi.fetch(VireoAbstractModel.buildRequest(api, 'create', gloss));
	};

	this.glossWithValue = function(value) {
        return VireoAbstractModel.findBy(api, cache, 'value', value);
	};

});
