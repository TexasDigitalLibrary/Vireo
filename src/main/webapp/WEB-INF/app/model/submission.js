var submissionModel = function ($q, WsApi) {

	return function Submission() {
		
		var submission = this;

		// additional model methods and variables


		submission.findFieldValuesByPredicate = function(predicate) {

			var foundFieldValues = [];

			for(var i in submission.fieldValues) {

				var fieldValue = submission.fieldValues[i];

				if(fieldValue.predicate.value == predicate.value) {
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

		submission.addFieldValue = function(predicate) {
			var fieldValue = {
				id: null,
				value: "",
				predicate: predicate
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