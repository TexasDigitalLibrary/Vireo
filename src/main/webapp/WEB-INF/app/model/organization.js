vireo.model("Organization", function ($q, RestApi) {

	return function Organization() {
		
		this.save = function() {
			
			var organization = this;
			
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
				console.log(res)
				if(res.meta.type == "INVALID") {
					angular.extend(organization, res.payload);
					console.log(organization);
				}
			});
			return promise;
		};
		
		// additional model methods and variables

		return this;
	};

});