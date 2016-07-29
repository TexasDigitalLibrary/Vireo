var submissionModel = function ($q, WsApi) {

	return function Submission() {
		
		var submission = this;

		// additional model methods and variables


		submission.findFieldValuesByFieldPredicate = function(fieldPredicate) {

			var foundFieldValues = [];

			for(var i in submission.fieldValues) {

				var fieldValue = submission.fieldValues[i];

				if(fieldValue.fieldPredicate.value == fieldPredicate.value) {
					foundFieldValues.push(fieldValue);
				}

			}

			return foundFieldValues;

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
			var fieldValue = {
				id: null,
				value: "",
				fieldPredicate: fieldPredicate
			};

			submission.fieldValues.push(fieldValue);

			return fieldValue;

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

		return submission;
	}

}

vireo.model("Submission", submissionModel);
vireo.model("StudentSubmission", submissionModel);