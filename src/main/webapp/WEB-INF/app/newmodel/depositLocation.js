vireo.model("DepositLocation", function () {

	var DepositLocation = function(data) {
		var depositLocation = this;
		angular.extend(depositLocation, data);
		return depositLocation;

	};

	return DepositLocation;

});