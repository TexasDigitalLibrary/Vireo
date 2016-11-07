vireo.model("FieldValue", function FieldValue(WsApi) {

	return function FieldValue() {
		
		// additional model methods and variables
		
		var fieldValue = this;
		
		fieldValue.save = function(submissionId) {
			angular.extend(fieldValue.getMapping().update, {
				'method': submissionId + '/update-field-value',
				'data': fieldValue
			});
			var promise = WsApi.fetch(fieldValue.getMapping().update);
			
			promise.then(function(response) {
				fieldValue.update(angular.fromJson(response.body).payload.FieldValue);
			});
			
			return promise;
		};

		return fieldValue;
	}

});