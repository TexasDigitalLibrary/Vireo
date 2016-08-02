vireo.model("Organization", function (RestApi) {

	return function Organization() {

		this.delete = function() {
			var organization = this;
			angular.extend(apiMapping.Organization.remove, {'data': organization}); //We use 'remove' in the mapping because delete is a js reserved word.
			var promise = RestApi.post(apiMapping.Organization.remove);
			promise.then(function(res) {
				if(res.meta.type == "INVALID") {
					angular.extend(organization, res.payload);
					console.log(organization);
				}
			});
			return promise;
		};

		return this;
	};

});
