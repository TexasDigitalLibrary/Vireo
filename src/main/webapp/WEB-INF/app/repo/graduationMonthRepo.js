vireo.repo("GraduationMonthRepo", function (api, GraduationMonth, WsApi) {

	var GraduationMonthRepo = function() {
		var graduationMonthRepo = this;
		graduationMonthRepo.model = GraduationMonth;
		graduationMonthRepo.mapping = api.GraduationMonth;
		return graduationMonthRepo;
	};

	return new GraduationMonthRepo();
	
});