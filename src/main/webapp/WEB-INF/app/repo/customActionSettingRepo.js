vireo.repo("CustomActionSettingRepo", function(AbstractAppRepo, api, CustomActionSetting, WsApi) {

	var CustomActionSettingRepo = function() {
		var customActionSettingRepo = this;
		angular.extend(customActionSettingRepo, new AbstractAppRepo(CustomActionSetting, api.customActionSetting));
		return customActionSettingRepo;
	};

	return new CustomActionSettingRepo();
});