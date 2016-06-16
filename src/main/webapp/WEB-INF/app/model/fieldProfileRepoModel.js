vireo.service("FieldProfileModel", function($q, WsApi, VireoAbstractModel) {

	var cache = {
		list  : [],
		ready: false
	};

	var api = {
		request: {
			endpoint  : '/private/queue',
			controller: 'settings/field-profile',
			method    : ''
		}
	};

	this.getAll = function(sync){
		cache.ready = sync ? !sync : cache.ready;
		VireoAbstractModel.getAllPromise(api, cache);
		return cache.list;
	};

	this.addFieldProfile = function(profile){
		return WsApi.fetch(VireoAbstractModel.buildRequest(api, 'createFieldProfile', profile));
	};

});
