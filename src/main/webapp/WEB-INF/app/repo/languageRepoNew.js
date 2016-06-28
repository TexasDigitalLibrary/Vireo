vireo.factory("LanguageRepoNew", function(AbstractAppRepo, api, Language) {

	var LanguageRepoNew = function() {
		var languageRepoNew	= this;
		angular.extend(languageRepoNew, new AbstractAppRepo(Language, api.languages));

		return languageRepoNew;

	};

	return new LanguageRepoNew();

});