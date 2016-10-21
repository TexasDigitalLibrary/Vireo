var submissionModel = function ($q, WsApi) {

	return function Submission() {
		
		var submission = this;

		// additional model methods and variables
		var getStubFieldValue = function(fieldPredicate) {
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
				fieldValues.push(getStubFieldValue(fieldPredicate));
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
			submission.fieldValues.push(getStubFieldValue(fieldPredicate));
		};

		submission.saveFieldValue = function(fieldValue) {

			angular.extend(this.getMapping().saveFieldValue, {
				method: submission.id+"/update-field-value",
				data: fieldValue
			});

			var promise = WsApi.fetch(this.getMapping().saveFieldValue);

			promise.then(function(rawApiResponse) {
				var updatedFieldValue = JSON.parse(rawApiResponse.body).payload.FieldValue;
				var index = submission.fieldValues.indexOf(fieldValue);
				submission.fieldValues[index] = updatedFieldValue;
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