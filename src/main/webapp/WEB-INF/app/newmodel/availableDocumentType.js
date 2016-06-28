vireo.service("AvailableDocumentType", function(AbstractAppModel, api) {

	var AvailableDocumentType = function(data) {
		var availableDocumentType = this;
		angular.extend(availableDocumentType, new AbstractAppModel(api.availableDocumentType, data));
		return availableDocumentType;

	};

	return AvailableDocumentType;

});