vireo.model("Organization", function Organization($q, WsApi, RestApi) {

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
			return promise;
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
				}
			});
			return promise;
		};

		this.addEmailWorkflowRule = function(templateId, recipient, submissionStateId) {

			var organization = this;
			angular.extend(apiMapping.Organization.addEmailWorkflowRule, {
				'method': organization.id+"/add-email-workflow-rule",
				'data': {
					templateId: templateId,
					recipient: recipient,
					submissionStateId: submissionStateId
				}
			});
			var promise = WsApi.fetch(apiMapping.Organization.addEmailWorkflowRule);
			
			return promise;

		};

		this.removeEmailWorkflowRule = function(rule) {

			var organization = this;
			angular.extend(apiMapping.Organization.removeEmailWorkflowRule, {
				'method': organization.id+"/remove-email-workflow-rule/"+rule.id,
			});
			var promise = WsApi.fetch(apiMapping.Organization.removeEmailWorkflowRule);
			
			return promise;	

		};

		this.changeEmailWorkflowRuleActivation = function(rule) {
			var organization = this;
			angular.extend(apiMapping.Organization.changeEmailWorkflowRuleActivation, {
				'method': organization.id+"/change-email-workflow-rule-activation/"+rule.id,
			});
			var promise = WsApi.fetch(apiMapping.Organization.changeEmailWorkflowRuleActivation);
			
			return promise;	
		};

		return this;

	};
});
