vireo.model("EmailTemplate", function () {

	var EmailTemplate = function(data) {
		var emailTemplate = this;
		angular.extend(emailTemplate, data);
		return emailTemplate;

	};

	return EmailTemplate;

});