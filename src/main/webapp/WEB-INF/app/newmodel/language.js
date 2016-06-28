vireo.service("Language", function(AbstractAppModel, api) {

	var Language = function(data) {
		var language = this;
		angular.extend(language, new AbstractAppModel(api.language, data));
		return language;
	};

	return Language;
});