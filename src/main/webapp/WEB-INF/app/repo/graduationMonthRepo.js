vireo.factory("GraduationMonthRepo", function(AbstractAppRepo, api, GraduationMonth, WsApi) {

	var GraduationMonthRepo = function() {
		var graduationMonthRepo = this;
		angular.extend(graduationMonthRepo, new AbstractAppRepo(GraduationMonth, api.graduationMonth));
		return graduationMonthRepo;
	};

	return new GraduationMonthRepo();
});