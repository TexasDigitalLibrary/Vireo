vireo.model("Organization", function ($q, RestApi) {

	return function Organization() {
		
		var organization = this;

		// additional model methods and variables

		// Override
		organization.save = function() {

			console.log(RestApi);

			var promise = $q(function(resolve) {
				if(organization.dirty()) {
					angular.extend(organization.getMapping().update, {data: organization});
					console.log(organization.getMapping().update);
					RestApi.post(organization.getMapping().update).then(function(res) {						
						resolve(res);
					});
				}
				else {
					var payload = {};
					payload[organization.constructor.name] = organization;
					resolve({
						body: angular.toJson({ 
							payload: payload,
							meta: {
								type: "SUCCESS"
							}
						})
					});
				}
			});
			promise.then(function(res) {
				if(angular.fromJson(res.body).meta.type == "INVALID") {
					angular.extend(organization, angular.fromJson(res.body).payload);
					console.log(organization);
				}
			});
			return promise;
		};

		return organization;
	}

});