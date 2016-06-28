vireo.factory("AvailableDocumentTypeRepo", function(AbstractAppRepo, api, AvailableDocumentType, WsApi) {

	var AvailableDocumentTypeRepo = function() {
		var availableDocumentTypeRepo = this;
		angular.extend(availableDocumentTypeRepo, new AbstractAppRepo(AvailableDocumentType, api.availableDocumentType));
		return availableDocumentTypeRepo;
	};

	return new AvailableDocumentTypeRepo();
});