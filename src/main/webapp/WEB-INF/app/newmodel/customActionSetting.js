vireo.service("CustomActionSetting", function(AbstractAppModel, api) {

	var CustomActionSetting = function(data) {
		var customActionSetting = this;
		angular.extend(customActionSetting, new AbstractAppModel(api.customActionSetting, data));
		return customActionSetting;

	};

	return CustomActionSetting;

});