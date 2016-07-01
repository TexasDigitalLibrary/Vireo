vireo.model("ConfigurableSetting", function ($sanitize, WsApi) {

	return function ConfigurableSetting() {

		// additional model methods and variables

		this.reset = function() {
			$sanitize(this.value).replace(new RegExp("&#10;", 'g'), "")
			angular.extend(this.mapping().reset, {data: this});
			return WsApi.fetch(this.mapping().reset);
		};

		return this;
	}

});