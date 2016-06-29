vireo.repo("CustomActionSettingRepo", function (api, CustomActionSetting, WsApi) {

	var CustomActionSettingRepo = function() {
		var customActionSettingRepo = this;
		customActionSettingRepo.constructor = CustomActionSetting;
		customActionSettingRepo.mapping = api.customActionSetting;
		return customActionSettingRepo;
	};

	return new CustomActionSettingRepo();
	
});