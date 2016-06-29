vireo.model("DepositLocation", function () {

	var DepositLocation = this;

	return function DepositLocation(data) {
		angular.extend(this, data);
		angular.extend(this, DepositLocation);
		return this;
	};

});