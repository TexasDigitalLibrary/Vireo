vireo.service("AbstractAppModel", function(AbstractModelNew) {

	var AbstractAppModel = function () {
		var abstractAppModel = this;
		angular.extend(abstractAppModel, new AbstractModelNew());

		// additional app level model methods and variables

		return abstractAppModel;
	};

	return AbstractAppModel;
});