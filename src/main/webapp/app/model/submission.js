var submissionModel = function ($filter, $q, ActionLog, FieldValue, FileService, Organization, EmailRecipient, EmailRecipientType, WsApi) {

    return function Submission() {

        var submission = this;

        submission.isValid = false;

        submission.actionLogListenPromise = null;

        submission.enableMergeCombinationOperation();

        var removeFieldValue = function (removedFieldValue) {
            for (var i in submission.fieldValues) {
                var fieldValue = submission.fieldValues[i];

                if (fieldValue.id === removedFieldValue.id) {
                    submission.fieldValues.splice(i, 1);
                    if (submission.primaryDocumentFieldValue !== undefined && submission.primaryDocumentFieldValue !== null && fieldValue.id === submission.primaryDocumentFieldValue.id) {
                        delete submission.primaryDocumentFieldValue;
                    }

                    // Check if the field is repeatable
                    var fieldProfile = submission.getFieldProfileByPredicate(fieldValue.fieldPredicate);

                    // Count remaining field values for this predicate
                    var remainingFieldValues = submission.getFieldValuesByFieldPredicate(fieldValue.fieldPredicate);

                    // If repeatable and no remaining field values, add a new one
                    if (fieldProfile.repeatable && remainingFieldValues.length === 0) {
                        submission.addFieldValue(fieldValue.fieldPredicate);
                    }
                    // If not repeatable, add a new field value
                    else if (!fieldProfile.repeatable) {
                        submission.addFieldValue(fieldValue.fieldPredicate);
                    }

                    break;
                }
            }
        };

        //populate fieldValues with models for existing values
        var instantiateFieldValues = function () {
            var fieldValues = angular.copy(submission.fieldValues);
            if (submission.fieldValues) {
                submission.fieldValues.length = 0;
            }
            angular.forEach(fieldValues, function (fieldValue) {
                fieldValue = new FieldValue(fieldValue);
                submission.prepareDatePopupWorkaround(fieldValue);
                submission.fieldValues.push(fieldValue);
            });
        };

        //populate actionLogs with models for existing values
        var instantiateActionLogs = function () {
            if (angular.isDefined(submission.actionLogs)) {
                var actionLogs = angular.copy(submission.actionLogs);
                if (submission.actionLogs) {
                    submission.actionLogs.length = 0;
                }

                angular.forEach(actionLogs, function (actionLog) {
                    actionLog = new ActionLog(actionLog);
                    submission.actionLogs.push(actionLog);
                });
            }
        };

        // additional model methods and variables
        var createEmptyFieldValue = function (fieldPredicate) {
            return new FieldValue({
                value: "",
                fieldPredicate: fieldPredicate
            });
        };

        var enrichDocumentTypeFieldValue = function (fieldValue) {
            if (!!fieldValue.value) {
                if (!fieldValue.fileInfo && fieldValue.value.length > 0) {
                    submission.fileInfo(fieldValue).then(function (response) {
                        fieldValue.fileInfo = angular.fromJson(response.body).payload.ObjectNode;
                    });
                }

                if (fieldValue.value.length > 0 && submission.getFileType(fieldValue.fieldPredicate) === 'PRIMARY') {
                    angular.extend(submission, {
                        primaryDocumentFieldValue: fieldValue
                    });
                }
            }
        };

        submission.fetchDocumentTypeFileInfo = function () {
            angular.forEach(submission.fieldValues, function (fieldValue) {
                if (fieldValue.fieldPredicate.documentTypePredicate) {
                    enrichDocumentTypeFieldValue(fieldValue);
                }
            });
        };

        submission.enableListeners = function (simple) {
            if (angular.isUndefined(submission.id)) {
                return;
            }

            submission.listen(function () {
                instantiateFieldValues();
                instantiateActionLogs();
            });

            var fieldValuesListen = apiMapping.Submission.fieldValuesListen;
            var fieldValuesRemovedListen = apiMapping.Submission.fieldValueRemovedListen;

            fieldValuesListen.method = submission.id + '/field-values';
            fieldValuesRemovedListen.method = submission.id + '/removed-field-values';

            submission.fieldValuesListenPromise = WsApi.listen(fieldValuesListen);
            submission.fieldValuesRemovedListenPromise = WsApi.listen(fieldValuesRemovedListen);

            submission.fieldValuesListenPromise.then(null, null, function (res) {
                var replacedFieldValue = false;
                var newFieldValue = angular.fromJson(res.body).payload.FieldValue;
                var emptyFieldValues = [];
                var fieldValue;

                submission.prepareDatePopupWorkaround(newFieldValue);

                for (var i in submission.fieldValues) {
                    fieldValue = submission.fieldValues[i];

                    if (fieldValue.fieldPredicate.id === newFieldValue.fieldPredicate.id) {
                        if (fieldValue.id) {
                            if (fieldValue.id === newFieldValue.id) {
                                angular.extend(fieldValue, newFieldValue);
                                replacedFieldValue = true;
                                break;
                            }
                        } else {
                            emptyFieldValues.push(fieldValue);
                        }
                    }
                }

                if (emptyFieldValues.length === 1) {
                    fieldValue = emptyFieldValues[0];
                    angular.extend(fieldValue, newFieldValue);
                    replacedFieldValue = true;
                }

                if (!replacedFieldValue) {
                    fieldValue = new FieldValue(newFieldValue);
                    submission.fieldValues.push(fieldValue);
                }

                if (fieldValue.fieldPredicate.documentTypePredicate) {
                    enrichDocumentTypeFieldValue(fieldValue);
                }
            });

            submission.fieldValuesRemovedListenPromise.then(null, null, function (res) {
                var removedFieldValue = angular.fromJson(res.body).payload.FieldValue;

                removeFieldValue(removedFieldValue);
            });

            if (simple !== true) {
                var actionLogListen = apiMapping.Submission.actionLogListen;
                var customActionValuesListen = apiMapping.Submission.customActionValuesListen;

                actionLogListen.method = submission.id + '/action-logs';
                customActionValuesListen.method = submission.id + '/custom-action-values';

                submission.actionLogListenPromise = WsApi.listen(actionLogListen);
                submission.customActionValuesListenPromise = WsApi.listen(customActionValuesListen);

                submission.actionLogListenReloadDefer = $q.defer();

                submission.actionLogListenPromise.then(null, null, function (res) {
                    var newActionLog = angular.fromJson(res.body).payload.ActionLog;

                    if (angular.isUndefined(submission.actionLogs)) {
                        submission.actionLogs = [];
                    }

                    submission.actionLogs.push(new ActionLog(newActionLog));
                    submission.actionLogListenReloadDefer.notify(submission.actionLogs);
                });

                submission.customActionValuesListenPromise.then(null, null, function (res) {
                    var replacedCustomActionValue = false;
                    var newCustomActionValue = angular.fromJson(res.body).payload.CustomActionValue;

                    for (var i in submission.customActionValues) {
                        if (submission.customActionValues[i].id === newCustomActionValue.id) {
                            angular.extend(submission.customActionValues[i], newCustomActionValue);
                            replacedCustomActionValue = true;
                            break;
                        }
                    }

                    if (!replacedCustomActionValue) {
                        cav = new CustomActionValue(newCustomActionValue);
                        submission.customActionValues.push(cav);
                    }
                });
            }
        };

        submission.before(function() {
            submission.organization = new Organization(submission.organization);

            instantiateFieldValues();

            // populate fieldValues with models for empty values
            angular.forEach(submission.submissionWorkflowSteps, function (submissionWorkflowStep) {
                angular.forEach(submissionWorkflowStep.aggregateFieldProfiles, function (fp) {
                    var fieldValuesByFieldPredicate = submission.getFieldValuesByFieldPredicate(fp.fieldPredicate);
                    if (!fieldValuesByFieldPredicate.length) {
                        submission.fieldValues.push(createEmptyFieldValue(fp.fieldPredicate));
                    }
                });
            });

            instantiateActionLogs();
        });

        submission.addComment = function (data) {
          angular.extend(apiMapping.Submission.addComment, {
              'method': submission.id + "/add-comment",
              'data': data
          });
          var promise = WsApi.fetch(apiMapping.Submission.addComment);
          promise.then(function (res) {
              if (res.meta && res.meta.status == "INVALID") {
                  submission.setValidationResults(res.payload.ValidationResults);
              }
          });
          return promise;
        };

        submission.sendEmail = function (data) {
            angular.extend(apiMapping.Submission.sendEmail, {
                'method': submission.id + "/send-email",
                'data': data
            });
            var promise = WsApi.fetch(apiMapping.Submission.sendEmail);
            promise.then(function (res) {
                if (res.meta && res.meta.status == "INVALID") {
                    submission.setValidationResults(res.payload.ValidationResults);
                }
            });
            return promise;
        };

        //Override
        submission.delete = function () {
            angular.extend(apiMapping.Submission.remove, {
                'method': "delete/" + submission.id
            });
            var promise = WsApi.fetch(apiMapping.Submission.remove);
            promise.then(function (res) {
                if (res.meta && res.meta.status == "INVALID") {
                    submission.setValidationResults(res.payload.ValidationResults);
                }
            });
            return promise;
        };

        submission.getFieldProfileByPredicateName = function (predicateValue) {
            var fieldProfile = null;
            for (var i in submission.submissionWorkflowSteps) {
                var submissionWorkflowStep = submission.submissionWorkflowSteps[i];
                for (var j in submissionWorkflowStep.aggregateFieldProfiles) {
                    var currentFieldProfile = submissionWorkflowStep.aggregateFieldProfiles[j];
                    if (currentFieldProfile.fieldPredicate.value === predicateValue) {
                        fieldProfile = currentFieldProfile;
                        break;
                    }
                }
            }
            return fieldProfile;
        };

        submission.getFieldProfileByPredicate = function (predicate) {
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

        submission.getPrimaryDocumentFieldProfile = function () {
            var fieldProfile = null;
            for (var i in submission.submissionWorkflowSteps) {
                var submissionWorkflowStep = submission.submissionWorkflowSteps[i];
                for (var j in submissionWorkflowStep.aggregateFieldProfiles) {
                    var currentFieldProfile = submissionWorkflowStep.aggregateFieldProfiles[j];
                    if (currentFieldProfile.fieldPredicate.value === '_doctype_primary') {
                        fieldProfile = currentFieldProfile;
                        break;
                    }
                }
            }
            return fieldProfile;
        };

        submission.getFieldValuesByFieldPredicate = function (fieldPredicate) {
            var fieldValues = [];
            for (var i in submission.fieldValues) {
                var fieldValue = submission.fieldValues[i];
                if (fieldValue.fieldPredicate.value == fieldPredicate.value) {
                    fieldValues.push(fieldValue);
                }
            }
            return fieldValues;
        };

        submission.getFieldValuesByInputType = function (inputType) {
            var fieldValues = [];
            for (var i in submission.submissionWorkflowSteps) {
                var workflowStep = submission.submissionWorkflowSteps[i];
                for (var j in workflowStep.aggregateFieldProfiles) {
                    var fieldProfile = workflowStep.aggregateFieldProfiles[j];
                    if (fieldProfile.inputType.name == inputType) {
                        var sfv = submission.getFieldValuesByFieldPredicate(fieldProfile.fieldPredicate);
                        for (var k in sfv) {
                            fieldValues.push(sfv[k]);
                        }
                    }
                }
            }
            return fieldValues;
        };

        submission.findFieldValueById = function (id) {
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

        submission.addFieldValue = function (fieldPredicate) {
            for (var i = submission.fieldValues.length - 1; i >= 0; i--) {
                var fv = submission.fieldValues[i];

                if (fv.fieldPredicate.id === fieldPredicate.id && (fv.value === "" || !fv.value)) {
                    submission.fieldValues.splice(i, 1);
                    break;
                }
            }

            var emptyFieldValue = createEmptyFieldValue(fieldPredicate);
            submission.fieldValues.push(emptyFieldValue);
            return emptyFieldValue;
        };

        submission.validateFieldValue = function (fieldValue, fieldProfile) {
            fieldValue.setIsValid(true);
            fieldValue.setValidationMessages([]);

            if ((!fieldValue.value || fieldValue.value === "") && !fieldProfile.optional && fieldProfile.enabled) {
                return $q(function (resolve) {
                    fieldValue.setIsValid(false);
                    fieldValue.addValidationMessage("This field is required");
                    resolve();
                });
            }

            angular.extend(this.getMapping().validateFieldValue, {
                method: submission.id + "/validate-field-value/" + fieldProfile.id,
                data: fieldValue
            });

            var promise = WsApi.fetch(this.getMapping().validateFieldValue);
            promise.then(function (response) {
                var responseObj = angular.fromJson(response.body);
                if (responseObj.meta.status === "INVALID") {
                    fieldValue.setIsValid(false);
                    angular.forEach(responseObj.payload.HashMap.value, function (value) {
                        fieldValue.addValidationMessage(value);
                    });
                }
            });
            return promise;

        };

        submission.saveFieldValue = function (fieldValue, fieldProfile) {
            var route = "/update-field-value/" + fieldProfile.id;
            fieldValue.setIsValid(true);
            fieldValue.setValidationMessages([]);

            if ((!fieldValue.value || fieldValue.value === "") && !fieldProfile.optional && fieldProfile.enabled) {
                return $q(function (resolve) {
                    fieldValue.setIsValid(false);
                    fieldValue.addValidationMessage("This field is required");
                    resolve();
                });
            } else if ((!fieldValue.value || fieldValue.value === "") && fieldProfile.optional && fieldProfile.enabled) {
                route = "/remove-field-value/";
            }

            angular.extend(this.getMapping().saveFieldValue, {
                method: submission.id + route,
                data: fieldValue
            });

            var promise = WsApi.fetch(this.getMapping().saveFieldValue);

            promise.then(function (response) {
                function addFieldValidationMessage(value) {
                    fieldValue.addValidationMessage(value);
                }

                var apiRes = angular.fromJson(response.body);
                if (apiRes.meta.status === "INVALID") {
                    fieldValue.setIsValid(false);
                    var messages = apiRes.payload.HashMap;
                    for (var property in messages) {
                        var messageValues = messages[property];
                        angular.forEach(messageValues, addFieldValidationMessage);
                    }
                } else {
                    var updatedFieldValue = null;

                    if (route === "/remove-field-value/") {
                        updatedFieldValue = submission.addFieldValue(fieldProfile.fieldPredicate);
                    } else {
                        updatedFieldValue = apiRes.payload.FieldValue;
                    }

                    fieldValue.setIsValid(true);
                    if (fieldValue.fieldPredicate.documentTypePredicate) {
                        delete fieldValue.fileInfo;
                        enrichDocumentTypeFieldValue(fieldValue);
                    }
                    var matchingFieldValues = {};
                    for (var i = submission.fieldValues.length - 1; i >= 0; i--) {
                        var currentFieldValue = submission.fieldValues[i];
                        if ((currentFieldValue.id === undefined || currentFieldValue.id === updatedFieldValue.id) && currentFieldValue.value == updatedFieldValue.value && currentFieldValue.fieldPredicate.id == updatedFieldValue.fieldPredicate.id) {

                            matchingFieldValues[i] = currentFieldValue;

                            for (var j in matchingFieldValues) {
                                if (currentFieldValue.file !== undefined) {
                                    matchingFieldValues[j].file = currentFieldValue.file;
                                }
                            }
                        }
                    }
                    var updated = false;
                    for (var k in matchingFieldValues) {
                        if (!updated) {
                            updated = true;
                            angular.extend(matchingFieldValues[k], updatedFieldValue);
                        } else {
                            submission.fieldValues.splice(k, 1);
                        }
                    }
                }

            });

            return promise;
        };

        submission.validate = function () {
            submission.isValid = false;

            var savePromises = [];

            angular.forEach(submission.fieldValues, function (fv) {
                var fieldProfile = submission.getFieldProfileByPredicate(fv.fieldPredicate);
                if (!fieldProfile.optional && fieldProfile.enabled || (fv.value !== "" && fieldProfile.optional && fieldProfile.enabled)) {
                    var savePromise = submission.validateFieldValue(fv, fieldProfile);
                    savePromises.push(savePromise);
                }
            });

            $q.all(savePromises).then(function () {
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

        submission.removeFieldValue = function (fieldValue) {
            angular.extend(this.getMapping().removeFieldValue, {
                method: submission.id + "/remove-field-value",
                data: fieldValue
            });

            var promise = WsApi.fetch(this.getMapping().removeFieldValue);

            promise.then(function (response) {
                var apiRes = angular.fromJson(response.body);
                if (apiRes.meta.status === 'SUCCESS') {
                    removeFieldValue(fieldValue);
                }
            });

            return promise;
        };

        submission.removeUnsavedFieldValue = function (fieldValue) {
            if (!fieldValue.id) {
                submission.fieldValues.splice(submission.fieldValues.indexOf(fieldValue), 1);
            }
        };

        submission.removeAllUnsavedFieldValuesByPredicate = function (fieldPredicate) {
            for (var i = submission.fieldValues.length - 1; i >= 0; i--) {
                var fieldValue = submission.fieldValues[i];
                if (fieldValue.fieldPredicate.id === fieldPredicate.id) {
                    submission.removeUnsavedFieldValue(fieldValue);
                }
            }
            submission.addFieldValue(fieldPredicate);
        };

        submission.saveReviewerNotes = function (reviewerNotes) {
            angular.extend(this.getMapping().saveReviewerNotes, {
                method: submission.id + "/update-reviewer-notes",
                data: {
                    'reviewerNotes': reviewerNotes
                }
            });
            var promise = WsApi.fetch(this.getMapping().saveReviewerNotes);
            return promise;
        };

        submission.fileInfo = function (fieldValue) {
            angular.extend(this.getMapping().fileInfo, {
                method: submission.id + '/file-info',
                data: {
                    'uri': fieldValue.value
                }
            });
            var promise = WsApi.fetch(this.getMapping().fileInfo);
            return promise;
        };

        submission.file = function (uri) {
            angular.extend(this.getMapping().file, {
                data: {
                    'uri': uri
                }
            });
            var promise = FileService.anonymousDownload(this.getMapping().file);
            return promise;
        };

        submission.removeFile = function (fieldValue) {
            angular.extend(this.getMapping().removeFile, {
                method: submission.id + '/' + fieldValue.id + '/remove-file'
            });
            var promise = WsApi.fetch(this.getMapping().removeFile);
            return promise;
        };

        submission.archiveFile = function (fieldValue) {
            angular.extend(this.getMapping().archiveFile, {
                method: submission.id + '/' + submission.getFileType(fieldValue.fieldPredicate) + "/archive-file",
                data: {
                    'uri': fieldValue.value,
                    'name': fieldValue.fileInfo.name
                }
            });
            var promise = WsApi.fetch(this.getMapping().archiveFile);
            return promise;
        };

        submission.renameFile = function (fieldValue) {
            angular.extend(this.getMapping().renameFile, {
                method: submission.id + '/' + submission.getFileType(fieldValue.fieldPredicate) + "/rename-file",
                data: {
                    'uri': fieldValue.value,
                    'newName': fieldValue.fileInfo.name
                }
            });
            var promise = WsApi.fetch(this.getMapping().renameFile);
            return promise;
        };

        submission.needsCorrection = function () {
            angular.extend(this.getMapping().needsCorrection, {
                method: submission.id + "/needs-correction"
            });
            var promise = WsApi.fetch(this.getMapping().needsCorrection);
            return promise;
        };

        submission.submitCorrections = function () {
            angular.extend(this.getMapping().submitCorrections, {
                method: submission.id + "/submit-corrections"
            });
            var promise = WsApi.fetch(this.getMapping().submitCorrections);
            return promise;
        };

        submission.updateCustomActionValue = function (customActionValue) {
            angular.extend(submission.getMapping().updateCustomActionValue, {
                method: submission.id + "/update-custom-action-value",
                data: customActionValue
            });
            return WsApi.fetch(submission.getMapping().updateCustomActionValue);
        };

        submission.changeStatus = function (submissionStatusName) {
            angular.extend(this.getMapping().changeStatus, {
                method: submission.id + "/change-status/" + submissionStatusName
            });
            var promise = WsApi.fetch(this.getMapping().changeStatus);
            return promise;
        };

        submission.publish = function (depositLocation) {
            angular.extend(this.getMapping().publish, {
                method: submission.id + "/publish/" + depositLocation.id
            });
            var promise = WsApi.fetch(this.getMapping().publish);
            return promise;
        };

        submission.submit = function () {
            return submission.changeStatus('Submitted');
        };

        submission.setSubmissionDate = function (newDate) {
            angular.extend(this.getMapping().submitDate, {
                method: submission.id + "/submit-date",
                data: newDate
            });
            var promise = WsApi.fetch(this.getMapping().submitDate);
            return promise;
        };

        submission.assign = function (assignee) {
            if (assignee === null) {
                assignee = {};
            }

            angular.extend(this.getMapping().assignTo, {
                method: submission.id + "/assign-to",
                data: assignee
            });
            var promise = WsApi.fetch(this.getMapping().assignTo);
            return promise;
        };

        submission.updateAdvisorApproval = function (approval) {
            angular.extend(this.getMapping().updateAdvisorApproval, {
                method: submission.id + "/update-advisor-approval",
                data: approval
            });
            var promise = WsApi.fetch(this.getMapping().updateAdvisorApproval);
            return promise;
        };

        submission.sendAdvisorEmail = function () {
            angular.extend(this.getMapping().sendAdvisorEmail, {
                method: submission.id + "/send-advisor-email"
            });
            var promise = WsApi.fetch(this.getMapping().sendAdvisorEmail);
            return promise;
        };

        submission.getFlaggedFieldProfiles = function () {
            var fieldProfiles = [];
            angular.forEach(submission.submissionWorkflowSteps, function (submissionWorkflowStep) {
                angular.forEach(submissionWorkflowStep.aggregateFieldProfiles, function (fp) {
                    if (fp.flagged)
                        fieldProfiles.push(fp);
                });
            });
            return fieldProfiles;
        };

        submission.getContactEmails = function () {
          return submission.organization.getWorkflowEmailContacts();
        };

        submission.addMessage = function (message) {
            angular.extend(this.getMapping().addMessage, {
                method: submission.id + "/add-message",
                data: message
            });
            var promise = WsApi.fetch(this.getMapping().addMessage);
            return promise;
        };

        submission.getFileType = function (fieldPredicate) {
            return fieldPredicate.value.substring(9).toUpperCase();
        };

        // This is a work-around of datepicker problems and ideally should be removed when either datepicker is fix or datepicker is replaced.
        submission.prepareDatePopupWorkaround = function (fieldValue) {
            if (angular.isDefined(fieldValue)) {
                var predicate = submission.findDatePredicate(fieldValue.fieldPredicate.value);

                if (predicate !== null) {
                    if (angular.isDefined(fieldValue.value) && fieldValue.value != null) {
                        // Work-around datepicker messing up time by applying the timezone to the value so that when datepicker applies the timezone they cancel out and get the correct value.
                        var stamp = Date.parse(fieldValue.value);

                        // Some browsers, like Firefox, do not support 'MMMM yyyy' formats for Date.parse().
                        if (isNaN(stamp) && predicate.format == 'MMMM yyyy') {
                            var split = fieldValue.value.match(/^(\S+) (\d+)$/);
                            if (!!split && split.length > 1) {
                                stamp = Date.parse(split[1] + ' 01, ' + split[2]);
                            }
                        }

                        if (isNaN(stamp)) {
                            // Fallback to unchanged value when unable to parse.
                            fieldValue.valuePopup = fieldValue.value;
                        } else {

                            // The timezoneoffset is in minutes and must be converted to milliseconds.
                            var offset = new Date().getTimezoneOffset() * 60000;
                            var offsetDate = new Date(stamp + offset);

                            // Must manually set the year month and day because Javascript date() now that the offset is applied.
                            if (typeof fieldValue.value == 'string') {
                                var split = fieldValue.value.split('-');

                                if (split.length == 3) {
                                    offsetDate.setFullYear(Number(split[0]));
                                    offsetDate.setMonth(Number(split[1]) - 1); // Month representation starts at 0 and not 1.
                                    offsetDate.setDate(Number(split[2]));
                                }
                            } else {
                                offsetDate.setFullYear(fieldValue.value.getFullYear());
                                offsetDate.setMonth(fieldValue.value.getMonth());
                                offsetDate.setDate(fieldValue.value.getDate());
                            }

                            offsetDate.setHours(0);
                            offsetDate.setMinutes(0);
                            offsetDate.setSeconds(0);
                            offsetDate.setMilliseconds(0);

                            if (Number.isNaN(offsetDate.getTime())) {
                                fieldValue.valuePopup = fieldValue.value;
                            } else {
                                fieldValue.valuePopup = offsetDate.toISOString();
                            }
                        }
                    } else {
                        fieldValue.valuePopup = fieldValue.value;
                    }
                }
            }
        };

        submission.findDatePredicate = function (match) {
            if (angular.isDefined(appConfig.datePredicates) && angular.isDefined(match)) {
                for (var i = 0; i < appConfig.datePredicates.length; i++) {
                    if (appConfig.datePredicates[i].how === 'exact') {
                        if (match === appConfig.datePredicates[i].name) {
                            return appConfig.datePredicates[i];
                        }
                    } else if (appConfig.datePredicates[i].how === 'start') {
                        if (match.startsWith(appConfig.datePredicates[i].name)) {
                            return appConfig.datePredicates[i];
                        }
                    }
                }
            }

            return null;
        };

        submission.saveDatePopupFieldValueWorkaround = function (fieldValue) {
            if (angular.isDefined(fieldValue)) {
                var predicate = submission.findDatePredicate(fieldValue.fieldPredicate.value);

                // Work-around datepicker messing up the time zone by stripping off the time and setting it to 0 to prevent Javascript date() from altering the day based on time zone.
                if (predicate !== null && angular.isDefined(fieldValue.valuePopup) && fieldValue.valuePopup != null) {
                    if (typeof fieldValue.valuePopup === 'object') {
                        var dateValue = new Date(fieldValue.valuePopup.getFullYear(), fieldValue.valuePopup.getMonth(), fieldValue.valuePopup.getDate(), 0, 0, 0);
                        dateValue.setMilliseconds(0);
                        fieldValue.value = $filter('date')(dateValue, predicate.database);
                    }
                }
            }
        };

        return submission;
    };
};

vireo.model("Submission", submissionModel);
vireo.model("StudentSubmission", submissionModel);
vireo.model("AdvisorSubmission", submissionModel);
