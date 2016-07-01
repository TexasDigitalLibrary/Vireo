// TODO: remove AbstractModel and refactor this!!!
vireo.factory("AbstractModelNew", function ($q, WsApi) {

	return function AbstractModelNew() {

		var abstractModel;

		var mapping;

		var defer = $q.defer();

		var listenCallbacks = [];

		var shadow = {};

		var cache;

		this.init = function(data, apiMapping) {

			abstractModel = this;

			mapping = apiMapping;

			if(data) {
				setData(data);
			}
			else if(cache !== undefined) {
				setData(cache);
			}
			else {
				
				WsApi.fetch(mapping.create).then(function(res) {
					cache = cache !== undefined ? cache : {};

					processResponse(res);

					listen();
				});
			}

		};

		this.ready = function() {
			return defer.promise;
		};

		this.save = function() {
			return $q(function(resolve) {
				if(abstractModel.dirty()) {
					angular.extend(mapping.update, {data: abstractModel});
					WsApi.fetch(mapping.update).then(function() {
						resolve(abstractModel);
					});
				}
				else {
					resolve(abstractModel);
				}
			});
			
		};

		this.delete = function() {
			console.log('delete');
		};

		this.listen = function(cb) {
			listenCallbacks.push(cb);
		};

		this.reset = function() {
			angular.extend(abstractModel, shadow);
		};

		this.dirty = function() {
			return angular.toJson(abstractModel) !== angular.toJson(shadow);
		};

		var setData = function(data) {
			angular.extend(abstractModel, data);
			shadow = angular.copy(abstractModel);
			defer.resolve();
		};

		var listen = function() {

			angular.extend(mapping.listen, {method: abstractModel.id});

			return WsApi.listen(mapping.listen).then(null, null, function(res) {
				processResponse(res);
				
				angular.forEach(listenCallbacks, function(cb) {
					cb();
				});

			});
		};

		var processResponse = function(res) {

			var resObj = angular.fromJson(res.body);

			var meta = resObj.meta;

			if(meta.type != 'ERROR') {
				var payload = resObj.payload;

				angular.forEach(payload, function(datum) {
					angular.extend(cache, datum);
				});

				angular.extend(abstractModel, cache);
				setData(cache);
			}
			else {
				abstractModel.reset();
			}
			
		};
		
		// additional core level model methods and variables
		
		return this;

	};
	
});