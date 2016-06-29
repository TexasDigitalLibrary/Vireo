vireo.model("CustomActionSetting", function () {

	var CustomActionSetting = this;

	return function CustomActionSetting(data) {
		angular.extend(this, data);
		angular.extend(this, CustomActionSetting);
		return this;
	};

});