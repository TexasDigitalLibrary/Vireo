var submissionModel = function ($q, WsApi) {

	return function Submission() {
		
		var submission = this;

		// additional model methods and variables
		var createEmptyFieldValue = function(fieldPredicate) {
			return {
				id: null,
				value: "",
				fieldPredicate: fieldPredicate
			}
		};

		submission.getFieldValuesByFieldPredicate = function(fieldPredicate) {

			var fieldValues = [];

			for(var i in submission.fieldValues) {
				var fieldValue = submission.fieldValues[i];
				if(fieldValue.fieldPredicate.value == fieldPredicate.value) {
					fieldValues.push(fieldValue);
				}
			}
			
			if (fieldValues.length === 0) {
				var emptyFieldValue = createEmptyFieldValue(fieldPredicate);
				submission.fieldValues.push(emptyFieldValue);
				fieldValues.push(emptyFieldValue);
			}

			return fieldValues;
		};

		submission.findFieldValueById = function(id) {

			var foundFieldValue = null;

			for(var i in submission.fieldValues) {
				var fieldValue = submission.fieldValues[i];
				if(fieldValue.id == id) {
					foundFieldValue = fieldValue;
					break;
				}
			}

			return foundFieldValue;
		};

		submission.addFieldValue = function(fieldPredicate) {
			submission.fieldValues.push(createEmptyFieldValue(fieldPredicate));
		};

		submission.saveFieldValue = function(fieldValue) {

			angular.extend(this.getMapping().saveFieldValue, {
				method: submission.id+"/update-field-value",
				data: fieldValue
			});

			var promise = WsApi.fetch(this.getMapping().saveFieldValue);

			promise.then(function(response) {
				var updatedFieldValue = angular.fromJson(response.body).payload.FieldValue;
				for(var i in submission.fieldValues) {
					var currentFieldValue = submission.fieldValues[i];
					if((currentFieldValue.id === null || currentFieldValue.id === updatedFieldValue.id) && currentFieldValue.value == updatedFieldValue.value && currentFieldValue.fieldPredicate.id == updatedFieldValue.fieldPredicate.id) {
						angular.extend(currentFieldValue, updatedFieldValue);
					}
				}
			});

			return promise;
		};
		
		submission.removeFieldValue = function(fieldValue) {

			angular.extend(this.getMapping().removeFieldValue, {
				method: submission.id+"/remove-field-value",
				data: fieldValue
			});

			var promise = WsApi.fetch(this.getMapping().removeFieldValue);

			return promise;
		};
		
		submission.fileInfo = function(uri) {

			angular.extend(this.getMapping().fileInfo, {
				data: {
					'uri': uri
				}
			});

			var promise = WsApi.fetch(this.getMapping().fileInfo);

			return promise;
		};

		return submission;
	}

}

vireo.model("Submission", submissionModel);
vireo.model("StudentSubmission", submissionModel);