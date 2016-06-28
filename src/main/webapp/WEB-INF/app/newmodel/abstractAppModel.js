vireo.service("AbstractAppModel", function(AbstractModelNew) {

	var AbstractAppModel = function (mapping, data) {
		var abstractAppModel = this;
		angular.extend(abstractAppModel, new AbstractModelNew(mapping, data));

		// additional app level model methods and variables

		return abstractAppModel;
	};

	return AbstractAppModel;
});