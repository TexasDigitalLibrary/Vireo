vireo.repo("DepositLocationRepo", function (api, DepositLocation, WsApi) {

	var DepositLocationRepo = function() {
		var depositLocationRepo = this;
		depositLocationRepo.model = DepositLocation;
		depositLocationRepo.mapping = api.DepositLocation;
		return depositLocationRepo;
	};

	return new DepositLocationRepo();
	
});