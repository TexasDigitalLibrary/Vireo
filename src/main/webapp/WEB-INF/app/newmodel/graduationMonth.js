vireo.model("GraduationMonth", function () {

	var GraduationMonth = function(data) {
		var graduationMonth = this;
		angular.extend(graduationMonth, data);
		return graduationMonth;

	};

	return GraduationMonth;

});