vireo.repo("LanguageRepo", function LanguageRepo(WsApi) {

	// additional repo methods and variables

	this.getProquestLanguageCodes = function() {

		return WsApi.fetch(this.mapping.proquest);
	};

	return this;

});