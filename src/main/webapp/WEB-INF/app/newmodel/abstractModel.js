// TODO: remove AbstractModel and refactor this!!!
vireo.factory("AbstractModelNew", function ($q, WsApi) {

	var sanitize = function(obj) {
		var copy = angular.copy(obj);
		delete copy.mapping;
		return copy;
	};

	return function AbstractModelNew() {

		var abstractModel;

		var defer = $q.defer();

		var listenCallbacks = [];

		var shadow = {};

		var cache;

		this.init = function(data) {

			abstractModel = this;

			if(data) {
				setData(data);
			}
			else if(cache !== undefined) {
				setData(cache);
			}
			else {
				
				WsApi.fetch(abstractModel.mapping.create).then(function(res) {
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
					angular.extend(abstractModel.mapping.update, {data: sanitize(abstractModel)});
					WsApi.fetch(abstractModel.mapping.update).then(function() {
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
			return angular.toJson(sanitize(abstractModel)) !== angular.toJson(sanitize(shadow));
		};

		var setData = function(data) {
			angular.extend(abstractModel, data);
			shadow = angular.copy(abstractModel);
			defer.resolve();
		};

		var listen = function() {

			angular.extend(abstractModel.mapping.listen, {method: abstractModel.id});

			return WsApi.listen(abstractModel.mapping.listen).then(null, null, function(res) {
				processResponse(res);
				
				angular.forEach(listenCallbacks, function(cb) {
					cb();
				});

			});
		};

		var processResponse = function(res) {

			var resObj = angular.fromJson(res.body);

			var meta = resObj.meta;

			console.log(meta);

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