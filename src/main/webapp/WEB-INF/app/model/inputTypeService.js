vireo.service("InputTypeService", function($q, WsApi) {

	var inputTypes = {}; //May be either {}, a promise, or a real payload object at any time.

	//Return a promise of real data, and caches the real data upon fulfillment.
	this.getAll = function() {
		if(angular.isDefined(this.inputTypes.inputTypesCache)){
			return $q.resolve(this.inputTypes.inputTypesCache).then(function(data){
				console.info('callback cache fired; mapping data', data);
				inputTypes.body = data.body;
			});
		}

		return wsResult = WsApi.fetch({
			'endpoint'  : '/private/queue',
			'controller': 'settings/input-types',
			'method'    : 'all'
		}).then(function(data){
			console.info('ws callback fired; mapping data', data);
			inputTypes.body = data.body;
		});
	}

	this.inputTypes = function(){
		this.getAll();
		return inputTypes;
	}

});
