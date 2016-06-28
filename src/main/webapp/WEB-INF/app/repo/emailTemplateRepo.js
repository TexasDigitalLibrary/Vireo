vireo.repo("EmailTemplateRepo", function(AbstractAppRepo, api, EmailTemplate, WsApi) {

	var EmailTemplateRepo = function() {
		var emailTemplateRepo = this;
		angular.extend(emailTemplateRepo, new AbstractAppRepo(EmailTemplate, api.emailTemplate));
		return emailTemplateRepo;
	};

	return new EmailTemplateRepo();
});