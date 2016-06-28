vireo.service("AbstractAppModel", function(AbstractModel2) {

	var AbstractAppModel = function (data) {
		var abstractAppModel = this;
		angular.extend(abstractAppModel, new AbstractModel2(data));

		// additional app level model methods and variables

		return abstractAppModel;
	};

	return AbstractAppModel;
});