vireo.repo("GraduationMonthRepo", function (api, GraduationMonth, WsApi) {

	var GraduationMonthRepo = function() {
		var graduationMonthRepo = this;
		graduationMonthRepo.constructor = GraduationMonth;
		graduationMonthRepo.mapping = api.graduationMonth;
		return graduationMonthRepo;
	};

	return new GraduationMonthRepo();
	
});