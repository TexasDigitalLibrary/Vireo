vireo.model("Language", function () {

	var Language = this;

	return function Language(data) {
		angular.extend(this, data);
		angular.extend(this, Language);
		return this;
	};

});