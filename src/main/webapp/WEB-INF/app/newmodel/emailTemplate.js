vireo.model("EmailTemplate", function () {

	var EmailTemplate = this;

	return function EmailTemplate(data) {
		angular.extend(this, data);
		angular.extend(this, EmailTemplate);
		return this;
	};

});