vireo.repo("DepositLocationRepo", function (api, DepositLocation, WsApi) {

	var DepositLocationRepo = function() {
		var depositLocationRepo = this;
		depositLocationRepo.constructor = DepositLocation;
		depositLocationRepo.mapping = api.depositLocation;
		return depositLocationRepo;
	};

	return new DepositLocationRepo();
	
});