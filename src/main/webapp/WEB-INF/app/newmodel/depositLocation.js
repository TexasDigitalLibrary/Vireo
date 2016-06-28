vireo.service("DepositLocation", function(AbstractAppModel, api) {

	var DepositLocation = function(data) {
		var depositLocation = this;
		angular.extend(depositLocation, new AbstractAppModel(api.depositLocation, data));
		return depositLocation;

	};

	return DepositLocation;

});