vireo.service("GraduationMonth", function(AbstractAppModel, api) {

	var GraduationMonth = function(data) {
		var graduationMonth = this;
		angular.extend(graduationMonth, new AbstractAppModel(api.graduationMonth, data));
		return graduationMonth;

	};

	return GraduationMonth;

});