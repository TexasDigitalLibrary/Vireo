var submissionModel = function ($q, FileApi, RestApi, WsApi) {

	return function Submission() {
		
		var submission = this;

		// additional model methods and variables
		var createEmptyFieldValue = function(fieldPredicate) {
			return {
				id: null,
				value: "",
				fieldPredicate: fieldPredicate
			};
		};

		//Override
		submission.delete = function() {
			var submission = this;
			angular.extend(apiMapping.Submission.remove, {'method': "delete/"+submission.id});
			var promise = WsApi.fetch(apiMapping.Submission.remove);
			promise.then(function(res) {
				if(res.meta.type == "INVALID") {
					submission.setValidationResults(res.payload.ValidationResults);
					console.log(submission);
				}
			});
			return promise;
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
			var emptyFieldValue = createEmptyFieldValue(fieldPredicate);
			submission.fieldValues.push(emptyFieldValue);
			return emptyFieldValue;
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
		
		submission.saveReviewerNotes = function(reviewerNotes) {

			angular.extend(this.getMapping().saveReviewerNotes, {
				method: submission.id+"/update-reviewer-notes",
				data: {
					'reviewerNotes': reviewerNotes
				}
			});

			var promise = WsApi.fetch(this.getMapping().saveReviewerNotes);

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
		
		submission.file = function(uri) {

			angular.extend(this.getMapping().file, {
				data: {
					'uri': uri
				}
			});

			var promise = FileApi.download(this.getMapping().file);

			return promise;
		};
		
		submission.removeFile = function(uri) {

			angular.extend(this.getMapping().removeFile, {
				data: {
					'uri': uri
				}
			});

			var promise = WsApi.fetch(this.getMapping().removeFile);

			return promise;
		};
		
		submission.renameFile = function(uri, newName) {

			angular.extend(this.getMapping().renameFile, {
				data: {
					'uri': uri,
					'newName': newName
				}
			});

			var promise = WsApi.fetch(this.getMapping().renameFile);

			return promise;
		};
		
		submission.needsCorrection = function() {

			angular.extend(this.getMapping().needsCorrection, {
				method: submission.id+"/needs-correction"
			});

			var promise = WsApi.fetch(this.getMapping().needsCorrection);

			return promise;
		};

		submission.changeStatus = function(status) {

			angular.extend(this.getMapping().changeStatus, {
				method: submission.id+"/change-status",
				data: status
			});

			var promise = WsApi.fetch(this.getMapping().changeStatus);

			return promise;
		};

		submission.listen(function() {
			console.log(submission);
		});

		return submission;
	}

}

vireo.model("Submission", submissionModel);
vireo.model("StudentSubmission", submissionModel);