vireo.model("Organization", function ($q, RestApi) {

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
