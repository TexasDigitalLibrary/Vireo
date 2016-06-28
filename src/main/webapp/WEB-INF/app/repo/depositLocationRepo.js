vireo.factory("DepositLocationRepo", function(AbstractAppRepo, api, DepositLocation, WsApi) {

	var DepositLocationRepo = function() {
		var depositLocationRepo = this;
		angular.extend(depositLocationRepo, new AbstractAppRepo(DepositLocation, api.depositLocation));
		return depositLocationRepo;
	};

	return new DepositLocationRepo();
});