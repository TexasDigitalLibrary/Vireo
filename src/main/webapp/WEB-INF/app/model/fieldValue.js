vireo.model("FieldValue", function FieldValue(WsApi) {

	return function FieldValue() {
		
		// additional model methods and variables
		
		var fieldValue = this;
		
		fieldValue.save = function(submissionId) {
			angular.extend(fieldValue.getMapping().update, {
				'method': submissionId + '/update-field-value',
				'data': fieldValue
			});
			return WsApi.fetch(fieldValue.getMapping().update);
		};

		return fieldValue;
	}

});