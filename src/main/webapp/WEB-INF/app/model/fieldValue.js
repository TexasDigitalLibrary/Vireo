vireo.model("FieldValue", function FieldValue(WsApi) {

	return function FieldValue() {
		
		// additional model methods and variables
		var fieldValue = this;
		var isValid = true;
		var validationMessage = "Is not valid!";

		fieldValue.setIsValid = function(valid) {
			isValid = valid;
		};

		fieldValue.isValid = function() {
			return isValid;
		};

		fieldValue.setValidationMessage = function(message) {
			validationMessage = message;
		};

		fieldValue.getValidationMessage = function() {
			return validationMessage;
		};

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