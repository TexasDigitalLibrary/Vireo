vireo.repo("LanguageRepo", function (api, Language, WsApi) {

	var LanguageRepo = function() {
		var languageRepo = this;

		languageRepo.constructor = Language;
		languageRepo.mapping = api.language;
		
		languageRepo.getProquestLanguageCodes = function() {
			return WsApi.fetch(api.language.proquest);
		};

		return languageRepo;
	};

	return new LanguageRepo();

});