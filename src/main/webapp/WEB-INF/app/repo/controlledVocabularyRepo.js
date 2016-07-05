core.repo("ControlledVocabularyRepo", function ControlledVocabularyRepo(RestApi, WsApi) {

	// additional repo methods and variables

	this.change = WsApi.listen(this.mapping.change);

	this.downloadCSV = function(controlledVocabulary) {
		angular.extend(this.mapping.downloadCSV, {
			'method': 'export/' + controlledVocabulary
		});
		return WsApi.fetch(this.mapping.downloadCSV);
	};

	this.uploadCSV = function(controlledVocabulary) {
		angular.extend(this.mapping.uploadCSV, {
			'method': 'import/' + controlledVocabulary
		});
		return WsApi.fetch(this.mapping.uploadCSV);
	};

	this.confirmCSV = function(file, controlledVocabulary) {
		angular.extend(this.mapping.confirmCSV, {
			'method': 'compare/' + controlledVocabulary,
			'file': file
		});
		return RestApi.post(this.mapping.confirmCSV);
	};

	this.cancel = function(controlledVocabulary) {
		angular.extend(this.mapping.cancel, {
			'method': 'cancel/' + controlledVocabulary
		});
		return WsApi.fetch(this.mapping.cancel);
	};

	this.status = function(controlledVocabulary) {
		angular.extend(this.mapping.status, {
			'method': 'status/' + controlledVocabulary
		});
		return WsApi.fetch(this.mapping.status);
	};

	return this;

});