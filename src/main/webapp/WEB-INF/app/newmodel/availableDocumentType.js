vireo.model("AvailableDocumentType", function () {

	var AvailableDocumentType = this;

	return function AvailableDocumentType(data) {
		angular.extend(this, data);
		angular.extend(this, AvailableDocumentType);
		return this;
	};

});