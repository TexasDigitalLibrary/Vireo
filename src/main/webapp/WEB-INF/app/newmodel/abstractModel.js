// TODO: remove AbstractModel and refactor this!!!
vireo.factory("AbstractModelNew", function ($q, WsApi) {

	var sanitize = function(obj) {
		var copy = angular.copy(obj);
		delete copy.mapping;
		return copy;
	}

	return function AbstractModelNew() {

		var defer = $q.defer();

		var cache;

		this.init = function(data) {

			var abstractModel = this;

			if(data) {
				angular.extend(this, data);
				defer.resolve();
			}
			if(cache !== undefined) {
				angular.extend(this, cache);
				defer.resolve();
			}
			else {
				
				WsApi.fetch(this.mapping.create).then(function(res) {
					cache = {};
					angular.extend(cache, angular.fromJson(res.body).payload.PersistentMap);
					angular.extend(abstractModel, cache);
					defer.resolve();
				});
			}

			WsApi.listen(this.mapping.listen).then(null, null, function(res) {
				console.log(abstractModel);
				console.log(res);
			});

		}

		this.ready = function() {
			return defer.promise;
		};

		this.save = function() {
			angular.extend(this.mapping.update, {data: sanitize(this)});
			return WsApi.fetch(this.mapping.update);
		};

		this.delete = function() {
			console.log('delete');
		};

		this.listen = function() {
			console.log('listen');
		};
		
		// additional core level model methods and variables
		
		return this;

	}
	
});