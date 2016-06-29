vireo.repo("AvailableDocumentTypeRepo", function (api, AvailableDocumentType, WsApi) {

	var AvailableDocumentTypeRepo = function() {
		var availableDocumentTypeRepo = this;
		availableDocumentTypeRepo.constructor = AvailableDocumentType;
		availableDocumentTypeRepo.mapping = api.availableDocumentType;
		return availableDocumentTypeRepo;
	};

	return new AvailableDocumentTypeRepo();
	
});