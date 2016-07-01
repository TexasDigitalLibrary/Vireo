vireo.repo("LanguageRepo", function (api, WsApi) {

	var LanguageRepo = function() {

		console.log(this);
		
		// additional repo methods and variables

		this.getProquestLanguageCodes = function() {
			return WsApi.fetch(api.Language.proquest);
		};

		return this;
	}

	return new LanguageRepo();

});