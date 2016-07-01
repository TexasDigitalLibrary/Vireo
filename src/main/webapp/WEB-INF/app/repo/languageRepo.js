vireo.repo("LanguageRepo", function (api, Language, WsApi) {

	var LanguageRepo = function () {
		var languageRepo = this;
		languageRepo.model = Language;
		languageRepo.mapping = api.Language;
		
		languageRepo.getProquestLanguageCodes = function() {
			return WsApi.fetch(api.Language.proquest);
		};

		// additional app level repo methods and variables

		return languageRepo;
	};

	return new LanguageRepo();

});