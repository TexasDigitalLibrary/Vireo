vireo.model("Configuration", function ($sanitize, WsApi) {

	return function Configuration() {

		// additional model methods and variables

		this.reset = function() {
			$sanitize(this.value).replace(new RegExp("&#10;", 'g'), "")
			angular.extend(this.getMapping().reset, {data: this});
			var promise = WsApi.fetch(this.getMapping().reset);
			promise.then(function(res) {
				console.log(angular.fromJson(res.body).payload);
			});
			return promise;
		};

		return this;
	}

});