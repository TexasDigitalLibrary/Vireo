vireo.model("Organization", function ($q, RestApi) {

	return function Organization() {
		
		var organization = this;

		// Override
		organization.save = function() {

			var promise = $q(function(resolve) {
				if(organization.dirty()) {
					angular.extend(organization.getMapping().update, {data: organization});
					RestApi.post(organization.getMapping().update).then(function(res) {						
						resolve(res);
					});
				}
				else {
					var payload = {};
					payload[organization.constructor.name] = organization;
					resolve({ 
						payload: payload,
						meta: {
							type: "SUCCESS"
						}
					});
				}
			});
			promise.then(function(res) {
				if(res.meta.type == "INVALID") {
					angular.extend(organization, angular.fromJson(res.body).payload);
					console.log(organization);
				}
			});
			return promise;
		};

		return organization;
	};

});