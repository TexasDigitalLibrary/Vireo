vireo.model("Organization", function ($rootScope, $q, RestApi) {

	return function Organization() {

		//Overrride
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
		};

		//Override
		this.delete = function() {
			var organization = this;
			angular.extend(apiMapping.Organization.remove, {'data': organization});
			var promise = RestApi.post(apiMapping.Organization.remove);
			promise.then(function(res) {
				if(res.meta.type == "INVALID") {
					organization.setValidationResults(res.payload.ValidationResults);
					console.log(organization);
				} else {
					$rootScope.$broadcast("deletedOrg", organization);
				}
			});
			return promise;
		};

		return this;

	};
});
