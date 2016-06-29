vireo.repo("AvailableDocumentTypeRepo", function (api, AvailableDocumentType, WsApi) {

	var AvailableDocumentTypeRepo = function() {
		var availableDocumentTypeRepo = this;
		availableDocumentTypeRepo.model = AvailableDocumentType;
		availableDocumentTypeRepo.mapping = api.availableDocumentType;
		return availableDocumentTypeRepo;
	};

	return new AvailableDocumentTypeRepo();
	
});