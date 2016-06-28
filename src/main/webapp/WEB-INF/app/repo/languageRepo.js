vireo.factory("LanguageRepo", function(AbstractAppRepo, api, Language, WsApi) {

	var LanguageRepo = function() {
		var languageRepo = this;
		angular.extend(languageRepo, new AbstractAppRepo(Language, api.language));

		languageRepo.getProquestLanguageCodes = function() {
			return WsApi.fetch(api.language.proquest);
		}

		return languageRepo;
	};

	return new LanguageRepo();
});