vireo.repo("EmailTemplateRepo", function (api, EmailTemplate, WsApi) {

	var EmailTemplateRepo = function() {
		var emailTemplateRepo = this;
		emailTemplateRepo.model = EmailTemplate;
		emailTemplateRepo.mapping = api.emailTemplate;
		return emailTemplateRepo;
	};

	return new EmailTemplateRepo();
	
});