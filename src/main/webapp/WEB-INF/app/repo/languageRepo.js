vireo.factory("LanguageRepo", function(AbstractAppRepo, api, Language, WsApi) {

	var LanguageRepo = function() {
		var languageRepo	= this;
		angular.extend(languageRepo, new AbstractAppRepo(Language, api.languages));

		languageRepo.getProquestLanguageCodes = function() {
			return WsApi.fetch(api.languages.proquest);
		}

		return languageRepo;

	};

	return new LanguageRepo();

});