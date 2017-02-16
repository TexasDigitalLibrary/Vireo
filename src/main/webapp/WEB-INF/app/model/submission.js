var submissionModel = function($q, FileApi, RestApi, FieldValue, WsApi) {

  return function Submission() {

    var submission = this;

    submission.isValid = false;

    submission.enableBeforeMethods();

    submission.before(function() {
      var fieldValues = angular.copy(submission.fieldValues);
      if (submission.fieldValues) submission.fieldValues.length = 0;

      //populate fieldValues with models for existing values
      angular.forEach(fieldValues, function(fieldValue) {
        submission.fieldValues.push(new FieldValue(fieldValue));
      });

      //populate fieldValues with models for empty values
      angular.forEach(submission.submissionWorkflowSteps, function(submissionWorkflowStep) {
        angular.forEach(submissionWorkflowStep.aggregateFieldProfiles, function(fp) {
          var fieldValuesByFieldPredicate = submission.getFieldValuesByFieldPredicate(fp.fieldPredicate);
          if (!fieldValuesByFieldPredicate.length) {
            submission.fieldValues.push(createEmptyFieldValue(fp.fieldPredicate));
          }
        });
      });

    });

    // additional model methods and variables
    var createEmptyFieldValue = function(fieldPredicate) {
      return new FieldValue({
        id: null,
        value: "",
        fieldPredicate: fieldPredicate
      });
    };

    //Override
    submission.delete = function() {
      var submission = this;
      angular.extend(apiMapping.Submission.remove, {
        'method': "delete/" + submission.id
      });
      var promise = WsApi.fetch(apiMapping.Submission.remove);
      promise.then(function(res) {
        if (res.meta && res.meta.type == "INVALID") {
          submission.setValidationResults(res.payload.ValidationResults);
        }
      });
      return promise;
    };

    submission.getFieldProfileByPredicate = function(predicate) {

      var fieldProfile = null;

      for (var i in submission.submissionWorkflowSteps) {
        var submissionWorkflowStep = submission.submissionWorkflowSteps[i];
        for (var j in submissionWorkflowStep.aggregateFieldProfiles) {
          var currentFieldProfile = submissionWorkflowStep.aggregateFieldProfiles[j];
          if (currentFieldProfile.fieldPredicate.id === predicate.id) {
            fieldProfile = currentFieldProfile;
            break;
          }
        }
      }

      return fieldProfile;

    };

    submission.getFieldValuesByFieldPredicate = function(fieldPredicate) {

      var fieldValues = [];

      for (var i in submission.fieldValues) {
        var fieldValue = submission.fieldValues[i];
        if (fieldValue.fieldPredicate.value == fieldPredicate.value) {
          fieldValues.push(fieldValue);
        }
      }

      return fieldValues;
    };

    submission.getFieldValuesByInputType = function(inputType) {

      var fieldValues = [];

      for (var i in submission.submissionWorkflowSteps) {
        var workflowStep = submission.submissionWorkflowSteps[i];
        for (var j in workflowStep.aggregateFieldProfiles) {
          var fieldProfile = workflowStep.aggregateFieldProfiles[j];
          if (fieldProfile.inputType.name == inputType) {
            angular.extend(fieldValues, submission.getFieldValuesByFieldPredicate(fieldProfile.fieldPredicate));
          }
        }
      }


      return fieldValues;
    };

    submission.findFieldValueById = function(id) {

      var foundFieldValue = null;

      for (var i in submission.fieldValues) {
        var fieldValue = submission.fieldValues[i];
        if (fieldValue.id == id) {
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

    submission.saveFieldValue = function(fieldValue, fieldProfile) {

      fieldValue.setIsValid(true);
      fieldValue.setValidationMessages([]);

      if ((!fieldValue.value || fieldValue.value === "") && !fieldProfile.optional) {
        return $q(function(resolve) {
          fieldValue.setIsValid(false);
          fieldValue.addValidationMessage("This field is required");
          resolve();
        });
      }

      angular.extend(this.getMapping().saveFieldValue, {
        method: submission.id + "/update-field-value/" + fieldProfile.id,
        data: fieldValue
      });

      var promise = WsApi.fetch(this.getMapping().saveFieldValue);

      promise.then(function(response) {

        var responseObj = angular.fromJson(response.body);

        if (responseObj.meta.type === "INVALID") {
          fieldValue.setIsValid(false);

          angular.forEach(responseObj.payload.HashMap.value, function(value) {
            fieldValue.addValidationMessage(value);
          });

        } else {
          fieldValue.setIsValid(true);
          var updatedFieldValue = responseObj.payload.FieldValue;
          for (var i in submission.fieldValues) {
            var currentFieldValue = submission.fieldValues[i];
            if ((currentFieldValue.id === null || currentFieldValue.id === updatedFieldValue.id) && currentFieldValue.value == updatedFieldValue.value && currentFieldValue.fieldPredicate.id == updatedFieldValue.fieldPredicate.id) {
              angular.extend(currentFieldValue, updatedFieldValue);
            }
          }
        }

      });

      return promise;
    };

    submission.validate = function() {

      submission.isValid = false;

      var savePromises = [];

      angular.forEach(submission.fieldValues, function(fv) {

        var fieldProfile = submission.getFieldProfileByPredicate(fv.fieldPredicate);

        if (!fieldProfile.optional || (fv.value !== "" && fieldProfile.optional)) {
          var savePromise = submission.saveFieldValue(fv, fieldProfile);
          savePromises.push(savePromise);
        }
      });

      $q.all(savePromises).then(function() {
        var valid = true;

        for (var i in submission.fieldValues) {
          var fv = submission.fieldValues[i];
          if (!fv.isValid()) {
            valid = false;
            break;
          }
        }

        submission.isValid = valid;

      });

    };

    submission.removeFieldValue = function(fieldValue) {

      angular.extend(this.getMapping().removeFieldValue, {
        method: submission.id + "/remove-field-value",
        data: fieldValue
      });

      var promise = WsApi.fetch(this.getMapping().removeFieldValue);

      return promise;
    };

    submission.saveReviewerNotes = function(reviewerNotes) {

      angular.extend(this.getMapping().saveReviewerNotes, {
        method: submission.id + "/update-reviewer-notes",
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
      console.log(this.getMapping().file);
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
        method: submission.id + "/needs-correction"
      });

      var promise = WsApi.fetch(this.getMapping().needsCorrection);

      return promise;
    };

    submission.updateCustomActionValue = function(customActionValue) {
      angular.extend(submission.getMapping().updateCustomActionValue, {
        method: submission.id + "/update-custom-action-value",
        data: customActionValue
      });
      return WsApi.fetch(submission.getMapping().updateCustomActionValue);
    };

    submission.changeStatus = function(submissionStateName) {

      angular.extend(this.getMapping().changeStatus, {
        method: submission.id + "/change-status/" + submissionStateName
      });

      var promise = WsApi.fetch(this.getMapping().changeStatus);

      return promise;
    };

    submission.submit = function() {
      return submission.changeStatus('Submitted');
    };

    submission.setSubmissionDate = function(newDate) {

      angular.extend(this.getMapping().submitDate, {
        method: submission.id + "/submit-date",
        data: newDate
      });

      var promise = WsApi.fetch(this.getMapping().submitDate);

      return promise;
    };

    submission.assign = function(assignee) {

      angular.extend(this.getMapping().assignTo, {
        method: submission.id + "/assign-to",
        data: assignee
      });

      var promise = WsApi.fetch(this.getMapping().assignTo);

      return promise;
    };

    submission.sendAdvisorEmail = function() {

      angular.extend(this.getMapping().sendAdvisorEmail, {
        method: submission.id + "/send-advisor-email",
      });

      var promise = WsApi.fetch(this.getMapping().sendAdvisorEmail);

      return promise;
    };

    return submission;
  };

};

vireo.model("Submission", submissionModel);
vireo.model("StudentSubmission", submissionModel);
vireo.model("AdvisorSubmission", submissionModel);
