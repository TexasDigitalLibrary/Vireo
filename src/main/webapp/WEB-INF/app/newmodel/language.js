vireo.service("Language", function(AbstractAppModel) {

	var Language = function(data) {

		var language = this;
		angular.extend(language, new AbstractAppModel(data));

		return language;

	};

	return Language;

});