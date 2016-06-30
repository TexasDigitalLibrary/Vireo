// TODO: remove AbstractModel and refactor this!!!
vireo.factory("AbstractModelNew", function ($q, WsApi) {

	var sanitize = function(obj) {
		var copy = angular.copy(obj);
		delete copy.mapping;
		return copy;
	};

	return function AbstractModelNew() {

		var defer = $q.defer();

		var cache;

		var shadow;

		this.setData = function(data) {
			angular.extend(this, data);
			this.shadow();
			defer.resolve();
		};

		this.init = function(data) {

			if(data) {
				this.setData(data);
			}
			else if(cache !== undefined) {
				this.setData(cache);
			}
			else {

				var abstractModel = this;
				
				WsApi.fetch(this.mapping.create).then(function(res) {
					cache = cache !== undefined ? cache : {};

					abstractModel.processResponse(res);

					abstractModel.listen();
				});
			}

		};

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
			var abstractModel = this;

			angular.extend(this.mapping.listen, {method: this.id});

			return WsApi.listen(this.mapping.listen).then(null, null, function(res) {
				abstractModel.processResponse(res);
			});
		};

		this.processResponse = function(res) {
			var payload = angular.fromJson(res.body).payload;

			angular.forEach(payload, function(datum) {
				angular.extend(cache, datum);
			});

			angular.extend(this, cache);
			this.setData(cache);
		};

		this.shadow = function() {
			shadow = angular.copy(this);
		};

		this.reset = function() {
			angular.extend(this, shadow);
		};
		
		// additional core level model methods and variables
		
		return this;

	};
	
});