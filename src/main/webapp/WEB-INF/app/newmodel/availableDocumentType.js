vireo.model("AvailableDocumentType", function () {

	var AvailableDocumentType = function(data) {
		var availableDocumentType = this;
		angular.extend(availableDocumentType, data);
		return availableDocumentType;

	};

	return AvailableDocumentType;

});