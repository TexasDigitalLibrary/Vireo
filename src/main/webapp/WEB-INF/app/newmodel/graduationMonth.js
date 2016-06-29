vireo.model("GraduationMonth", function () {

	var GraduationMonth = this;

	return function GraduationMonth(data) {
		angular.extend(this, data);
		angular.extend(this, GraduationMonth);
		return this;
	};

});