vireo.model("CustomActionSetting", function () {

	var CustomActionSetting = function(data) {
		var customActionSetting = this;
		angular.extend(customActionSetting, data);
		return customActionSetting;

	};

	return CustomActionSetting;

});