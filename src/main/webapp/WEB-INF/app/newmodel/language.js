vireo.model("Language", function () {

	var Language = function(data) {
		var language = this;
		angular.extend(language, data);
		return language;
	};

	return Language;

});