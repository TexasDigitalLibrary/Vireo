vireo.service("AbstractAppModel", function(AbstractModelNew) {

	var AbstractAppModel = function (data) {
		var abstractAppModel = this;
		angular.extend(abstractAppModel, new AbstractModelNew(data));

		// additional app level model methods and variables

		return abstractAppModel;
	};

	return AbstractAppModel;
});