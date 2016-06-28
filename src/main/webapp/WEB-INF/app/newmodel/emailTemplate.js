vireo.model("EmailTemplate", function(AbstractAppModel, api) {

	var EmailTemplate = function(data) {
		var emailTemplate = this;
		angular.extend(emailTemplate, new AbstractAppModel(api.emailTemplate, data));
		return emailTemplate;

	};

	return EmailTemplate;

});