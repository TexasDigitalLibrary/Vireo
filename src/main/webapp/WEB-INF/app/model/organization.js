vireo.model("Organization", function (WsApi) {

	return function Organization() {

		this.delete = function() {
			var organization = this;
			angular.extend(apiMapping.Organization.remove, {'method': 'delete/' + organization.id}); //We use 'remove' in the mapping because delete is a js reserved word.
			var promise = WsApi.fetch(apiMapping.Organization.remove);
			promise.then(function(res) {
				if(angular.fromJson(res.body).meta.type == "INVALID") {
					angular.extend(abstractModel, angular.fromJson(res.body).payload);
					// console.log(abstractModel);
				}
			});
			return promise;
		};

		return this;
	};

});
