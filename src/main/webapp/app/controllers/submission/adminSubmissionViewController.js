vireo.controller("AdminSubmissionViewController", function ($anchorScroll, $controller, $location, $route, $routeParams, $scope, DepositLocationRepo, EmailRecipient, EmailRecipientType, EmailTemplateRepo, EmbargoRepo, FieldPredicateRepo, FieldValue, FileUploadService, SidebarService, SubmissionRepo, SubmissionStatuses, SubmissionStatusRepo, UserRepo, UserService, UserSettings, WsApi) {

    angular.extend(this, $controller('AbstractController', {
        $scope: $scope
    }));

    $scope.updateActionLogLimit = function () {
        $scope.actionLogCurrentLimit = $scope.actionLogCurrentLimit === $scope.actionLogLimit ? $scope.submission.actionLogs.length : $scope.actionLogLimit;
    };

    $scope.fieldPredicates = FieldPredicateRepo.getAll();

    $scope.embargoes = EmbargoRepo.getAll();

    var userSettings = new UserSettings();

    var submissionStatuses = SubmissionStatusRepo.getAll();

    var depositLocations = DepositLocationRepo.getAll();

    $scope.emailTemplates = EmailTemplateRepo.getAll();

    EmailTemplateRepo.ready().then(function() {

        var addDefaultTemplate = true;
        for (var i in $scope.emailTemplates) {
            var template = $scope.emailTemplates[i];
            if (template.name === "Choose a Message Template") {
                addDefaultTemplate = false;
                break;
            }
        }

        if (addDefaultTemplate) {
            $scope.emailTemplates.unshift({
                name: "Choose a Message Template"
            });
        }

    });

    var surgicalFieldValueUpdate = function(submission) {
        // remove field values that are document type field predicates
        for(var i = $scope.submission.fieldValues.length - 1; i >= 0; i--) {
            var fieldValue = $scope.submission.fieldValues[i];
            if(fieldValue.fieldPredicate.documentTypePredicate) {
                $scope.submission.fieldValues.splice(i, 1);
            }
        }
        // add field values of response that are document type field predicates
        angular.forEach(submission.fieldValues, function (fieldValue) {
            if(fieldValue.fieldPredicate.documentTypePredicate) {
                $scope.submission.fieldValues.push(new FieldValue(fieldValue));
            }
        });
        // update current submissions status
        angular.extend($scope.submission.submissionStatus, submission.submissionStatus);
        // update current assignee
        angular.extend($scope.submission, { assignee : submission.assignee });
        // update current submission date
        angular.extend($scope.submission, { submissionDate : submission.submissionDate });
        // fetch file info
        $scope.submission.fetchDocumentTypeFileInfo();
    };

    $scope.loaded = true;

    $scope.addCommentModal = {};

    $scope.addFileData = {};

    $scope.errorMessage = "";

    $scope.dropZoneText = "Drop a file or click arrow";

    SubmissionRepo.fetchSubmissionById($routeParams.id).then(function(submission) {
      
        $scope.submission = submission;

        WsApi.listen("/channel/submission/" + $scope.submission.id).then(null, null, function(res) {
            var apiRes = angular.fromJson(res.body);
            surgicalFieldValueUpdate(apiRes.payload.Submission);
        });

        $scope.title = $scope.submission.submitter.lastName + ', ' + $scope.submission.submitter.firstName + ' (' + $scope.submission.organization.name + ')';

        $scope.submission.fetchDocumentTypeFileInfo();

        var hasPrimaryDocumentFieldValue = function () {
            return $scope.submission.primaryDocumentFieldValue !== undefined && $scope.submission.primaryDocumentFieldValue !== null;
        };

        var primaryDocumentFileName = function() {
            return hasPrimaryDocumentFieldValue() ? $scope.submission.primaryDocumentFieldValue.fileInfo !== undefined ? $scope.submission.primaryDocumentFieldValue.fileInfo.name : '' : '';
        };

        $scope.hasPrimaryDocument = function () {
            return hasPrimaryDocumentFieldValue() && $scope.submission.primaryDocumentFieldValue.id !== undefined;
        };

        $scope.resetCommentModal = function (addCommentModal) {
          $scope.closeModal();
          
          addCommentModal.adding = false;
          addCommentModal.commentVisibility = userSettings.notes_mark_comment_as_private_by_default ? "private" : "public";
          addCommentModal.recipientEmail = '';
          addCommentModal.recipientEmails = userSettings.notes_email_student_by_default === "true" ? [new EmailRecipient({
            name: "Submitter",
            type: EmailRecipientType.SUBMITTER,
            data: "Submitter"
          })] : [];
          addCommentModal.ccRecipientEmail = '';

          addCommentModal.ccRecipientEmails = [];
          for(var i in $scope.submission.getContactEmails()) {
              var contact_email = $scope.submission.getContactEmails()[i];
              if((userSettings.notes_email_student_by_default === "true")&&(contact_email.type==="SUBMITTER")){
                  addCommentModal.ccRecipientEmails.push(contact_email);
              }
              if((userSettings.notes_cc_student_advisor_by_default === "true")&&(contact_email.type==="ADVISOR")){
                  addCommentModal.ccRecipientEmails.push(contact_email);
              }
          }

          addCommentModal.sendEmailToRecipient = (addCommentModal.commentVisibility === "public" || userSettings.notes_email_student_by_default === "true") || (userSettings.notes_cc_student_advisor_by_default === "true");
          addCommentModal.sendEmailToCCRecipient = userSettings.notes_cc_student_advisor_by_default === "true";
          addCommentModal.subject = "";
          addCommentModal.message = "";
          addCommentModal.actionLogCurrentLimit = $scope.actionLogLimit;
          addCommentModal.selectedTemplate = $scope.emailTemplates[0];
          addCommentModal.needsCorrection = userSettings.notes_flag_submission_as_needs_corrections_by_default === "true";
      };

      $scope.resetCommentModal($scope.addCommentModal);

      $scope.addComment = function (addCommentModal) {
        addCommentModal.adding = true;
        $scope.submission.addComment(addCommentModal).then(function () {
          if (addCommentModal.needsCorrection) {
            $scope.submission.changeStatus(SubmissionStatuses.NEEDS_CORRECTIONS);
          }
          $scope.resetCommentModal(addCommentModal);
        });
      };

      $scope.disableAddComment = function () {
          var disable = true;
          if ($scope.addCommentModal.commentVisibility == 'public') {
              if ($scope.addCommentModal.sendEmailToRecipient) {
                  if ($scope.addCommentModal.sendEmailToCCRecipient) {
                      disable = $scope.addCommentModal.recipientEmails.length === 0 || 
                                $scope.addCommentModal.ccRecipientEmails.length === 0 || 
                                $scope.addCommentModal.subject === undefined || 
                                $scope.addCommentModal.subject === "" || 
                                $scope.addCommentModal.message === undefined ||
                                $scope.addCommentModal.message === "";
                  } else {
                      disable = $scope.addCommentModal.recipientEmails.length === 0 || 
                                $scope.addCommentModal.subject === undefined || 
                                $scope.addCommentModal.subject === "" || 
                                $scope.addCommentModal.message === undefined ||
                                $scope.addCommentModal.message === "";
                  }
              }
          } else {
              if ($scope.addCommentModal.commentVisibility == 'private') {
                  disable = $scope.addCommentModal.subject === undefined || 
                          $scope.addCommentModal.subject === "" || 
                          $scope.addCommentModal.message === undefined ||
                          $scope.addCommentModal.message === "";
              }
          }
          return disable;
      };

      $scope.addEmailAddressee = function (emails, formField) {

        var recipient = formField.$$rawModelValue;

        if (recipient) {
          
          if(typeof recipient === 'string') {

            if(!$scope.validateEmailAddressee(formField)) return;            

            recipient = new EmailRecipient({
              name: recipient,
              type: EmailRecipientType.PLAIN_ADDRESS,
              data: recipient
            });
          }
          
          emails.push(recipient);

          //This is not ideal, as it assumes the attr name and attr ngModel are the same.
          $scope[formField.$$attr.name+"Invalid"] = false;
          $scope.addCommentModal[formField.$$attr.name] = "";
          $scope.addFileData[formField.$$attr.name] = "";
        }
      };

      $scope.validateEmailAddressee = function(formField) {
        var valueIsContact = false;
        if(typeof formField.$$rawModelValue !== 'string') {
          var allContacts = submission.getContactEmails();
          for(var i in allContacts) {
            var contact = allContacts[i];
            if(formField.$$rawModelValue.type === contact.type) {
              valueIsContact = true;
              break;
            }
          }
        }        
        $scope[formField.$$attr.name+"Invalid"] = formField.$invalid && !valueIsContact;
        return  !$scope[formField.$$attr.name+"Invalid"];
      };

      $scope.isEmailAddresseeInvalid = function(formField) {
        return formField.$invalid && $scope[formField.$$attr.name+"Invalid"];
      };

      $scope.removeEmailAddressee = function (email,destinationModel) {
          var removeIndex = destinationModel.indexOf(email);
          destinationModel.splice(removeIndex,1);
      };

      $scope.emailValidationPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        $scope.showTab = function (workflowStep) {
            var show = false;
            for (var i in workflowStep.aggregateFieldProfiles) {
                if (workflowStep.aggregateFieldProfiles[i].inputType.name !== 'INPUT_FILE') {
                    show = true;
                    break;
                }
            }
            return show;
        };

        $scope.getTabPath = function (path) {
            return path + "/" + $scope.submission.id;
        };

        $scope.editReviewerNotes = function () {
            $scope.editingReviewerNotes = true;
            $scope.reviewerNotes = angular.copy($scope.submission.reviewerNotes);
        };

        $scope.saveReviewerNotes = function () {
            $scope.savingReviewerNotes = true;
            $scope.editingReviewerNotes = false;
            $scope.submission.saveReviewerNotes($scope.submission.reviewerNotes).then(function (response) {
                $scope.savingReviewerNotes = false;
            });
        };

        $scope.cancelReviewerNotes = function () {
            $scope.editingReviewerNotes = false;
            $scope.submission.reviewerNotes = angular.copy($scope.reviewerNotes);
        };

        $scope.getFile = function (fieldValue) {
            $scope.submission.file(fieldValue.value).then(function (data) {
                saveAs(new Blob([data], {
                    type: fieldValue.fileInfo.type
                }), fieldValue.fileInfo.name);
            });
        };

        $scope.getFileType = function (fieldPredicate) {
            return FileUploadService.getFileType(fieldPredicate);
        };

        $scope.isPrimaryDocument = function (fieldPredicate) {
            return $scope.getFileType(fieldPredicate) == 'PRIMARY';
        };

        $scope.deleteDocumentFieldValue = function (fieldValue) {
            fieldValue.updating = true;
            FileUploadService.removeFile($scope.submission, fieldValue).then(function () {
                $scope.closeModal();
                $scope.confirm = false;
                delete fieldValue.updating;
            });
        };

        $scope.saveDocumentFieldValue = function (fieldValue) {
            fieldValue.updating = true;
            $scope.closeModal();
            $scope.submission.renameFile(fieldValue).then(function (response) {
                fieldValue.value = angular.fromJson(response.body).meta.message;

                var fieldProfile = $scope.submission.getFieldProfileByPredicate(fieldValue.fieldPredicate);

                $scope.submission.saveFieldValue(fieldValue, fieldProfile).then(function (response) {
                    if (angular.fromJson(response.body).meta.status === "INVALID") {
                        fieldValue.refresh();
                    }
                    fieldValue.updating = false;
                });
            });
        };

        $scope.confirm = false;

        $scope.toggleConfirm = function () {
            $scope.confirm = !$scope.confirm;
        };

        $scope.cancel = function (fieldValue) {
            $scope.closeModal();
            fieldValue.refresh();
        };

        $scope.getDocumentTypePredicates = function () {
            var documentTypePredicates = [];
            for (var i in $scope.submission.submissionWorkflowSteps) {
                for (var j in $scope.submission.submissionWorkflowSteps[i].aggregateFieldProfiles) {
                    var fieldProfile = $scope.submission.submissionWorkflowSteps[i].aggregateFieldProfiles[j];
                    if (fieldProfile.fieldPredicate.documentTypePredicate && !containsPredicate(documentTypePredicates, fieldProfile.fieldPredicate)) {
                        documentTypePredicates.push(fieldProfile.fieldPredicate);
                    }
                }
            }
            return documentTypePredicates;
        };

        var containsPredicate = function (array, predicate) {
            return array.map(function(p) { return p.id; }).indexOf(predicate.id) >= 0;
        };

        $scope.getPattern = function (doctype) {
            var pattern = "*";
            var fieldPredicate;
            var i;

            for(i in $scope.fieldPredicates) {
                if($scope.fieldPredicates[i].value === doctype) {
                    fieldPredicate = $scope.fieldPredicates[i];
                    break;
                }
            }

            if (fieldPredicate !== undefined) {
                var fieldProfile = $scope.submission.getFieldProfileByPredicate(fieldPredicate);
                if (angular.isDefined(fieldProfile.controlledVocabulary)) {
                    var cv = fieldProfile.controlledVocabulary;
                    pattern = "";
                    for (i in cv.dictionary) {
                        var word = cv.dictionary[i];
                        pattern += pattern.length > 0 ? (",." + word.name) : ("." + word.name);
                    }
                }
            }

            return pattern;
        };

        $scope.queueUpload = function (files) {
            $scope.errorMessage = "";
            $scope.addFileData.files = files;
        };

        $scope.removeFiles = function () {
            $scope.errorMessage = "";
            delete $scope.addFileData.files;
        };

        var uploadFailed = function(fieldValue, reason) {
            $scope.errorMessage = "Upload Failed" + (reason ? ": " + reason : "") + ".";
            $scope.addFileData.uploading = false;
            fieldValue.uploading = false;
            fieldValue.setIsValid(false);
            if (fieldValue.fileInfo !== undefined && fieldValue.fileInfo.uploaded === true) {
                delete fieldValue.fileInfo.uploaded;
            }
            fieldValue.refresh();
        };

        $scope.resetFileData = function () {
            $scope.closeModal();
            $scope.errorMessage = "";
            $scope.removeFiles();
            
            $scope.addFileData.uploading = false;
            $scope.addFileData.recipientEmail = '';
            $scope.addFileData.recipientEmails = userSettings.attachment_email_student_by_default === "true" ? [new EmailRecipient({
                name: "Submitter",
                type: EmailRecipientType.SUBMITTER,
                data: "Submitter"
              })] : [];
            $scope.addFileData.ccRecipientEmail = '';

            $scope.addFileData.ccRecipientEmails = [];
            for(var i in $scope.submission.getContactEmails()) {
                var contact_email = $scope.submission.getContactEmails()[i];
                if((userSettings.attachment_email_student_by_default === "true")&&(contact_email.type==="SUBMITTER")){
                    $scope.addFileData.ccRecipientEmails.push(contact_email);
                }
                if((userSettings.attachment_cc_student_advisor_by_default === "true")&&(contact_email.type==="ADVISOR")){
                    $scope.addFileData.ccRecipientEmails.push(contact_email);
                }
            }

            $scope.addFileData.sendEmailToRecipient = (userSettings.attachment_email_student_by_default === "true") || (userSettings.attachment_cc_student_advisor_by_default === "true");
            $scope.addFileData.sendEmailToCCRecipient = userSettings.attachment_cc_student_advisor_by_default === "true";
            $scope.addFileData.subject = "";
            $scope.addFileData.message = "";
            $scope.addFileData.selectedTemplate = $scope.emailTemplates[0];
            $scope.addFileData.needsCorrection = userSettings.attachment_flag_submission_as_needs_corrections_by_default === "true";

        };

        $scope.resetFileData();
        
        $scope.submitAddFile = function () {

            $scope.addFileData.uploading = true;

            var fieldValue;

            if($scope.addFileData.addFileSelection === 'replace') {
                if($scope.submission.primaryDocumentFieldValue !== undefined) {
                    fieldValue = $scope.submission.primaryDocumentFieldValue;
                } else {
                    for(var i in $scope.fieldPredicates) {
                        var fieldPredicate = $scope.fieldPredicates[i];
                        if(fieldPredicate.value === '_doctype_primary') {
                            fieldValue = new FieldValue({
                                fieldPredicate: fieldPredicate
                            });
                            break;
                        }
                    }
                }
            } else {
                fieldValue = new FieldValue({
                    fieldPredicate: $scope.addFileData.fieldPredicate
                });
            }

            fieldValue.file = $scope.addFileData.files[0];

            FileUploadService.uploadFile($scope.submission, fieldValue).then(function (response) {
                if (response.data.meta.status === 'SUCCESS') {
                    var fieldProfile = $scope.submission.getFieldProfileByPredicate(fieldValue.fieldPredicate);

                    if ($scope.addFileData.addFileSelection === 'replace' && $scope.hasPrimaryDocument()) {
                        FileUploadService.archiveFile($scope.submission, $scope.submission.primaryDocumentFieldValue);
                    }

                    fieldValue.value = response.data.meta.message;

                    $scope.submission.saveFieldValue(fieldValue, fieldProfile).then(function (response) {
                        var apiRes = angular.fromJson(response.body);
                        if (apiRes.meta.status === "INVALID") {
                            if (apiRes.meta.message !== undefined) {
                                uploadFailed(fieldValue, apiRes.meta.message);
                            }
                            else {
                                uploadFailed(fieldValue, false);
                            }
                        } else {
                            if ($scope.addFileData.sendEmailToRecipient) {
                                $scope.submission.sendEmail({
                                    subject: $scope.addFileData.subject,
                                    message: $scope.addFileData.message,
                                    recipientEmails: $scope.addFileData.recipientEmails,
                                    ccRecipientEmails: $scope.addFileData.ccRecipientEmails,
                                    sendEmailToRecipient: $scope.addFileData.sendEmailToRecipient,
                                    sendEmailToCCRecipient: $scope.addFileData.sendEmailToCCRecipient
                                }).then(function () {
                                    $scope.resetFileData();
                                });
                            } else {
                                $scope.resetFileData();
                            }
                        }
                    });
                }
                else {
                    if (response.payload !== undefined && typeof response.payload  == "object" && response.payload.meta.message !== undefined) {
                        uploadFailed(fieldValue, response.payload.meta.message);
                    }
                    else {
                        uploadFailed(fieldValue, false);
                    }
                }

            }, function (response) {
                var reason = false;
                var status = response.meta.status;
                if (response.payload !== undefined && typeof response.payload  == "object" && response.payload.meta.message !== undefined) {
                    reason = response.payload.meta.message;
                    status = response.payload.meta.status;
                }
                else if (response.status !== undefined) {
                    status = response.status;
                }
                console.log('Error status: ' + status);
                uploadFailed(fieldValue, reason);
            }, function (progress) {
                $scope.addFileData.progress = progress;
            });

            if ($scope.addFileData.needsCorrection) {
                $scope.submission.needsCorrection();
            }

        };

        $scope.disableSubmitAddFile = function () {
            var disable = true;
            if ($scope.addFileData.addFileSelection == 'replace') {
                if ($scope.addFileData.sendEmailToRecipient) {
                    if ($scope.addFileData.sendEmailToCCRecipient) {
                        disable = $scope.addFileData.files === undefined ||
                                  $scope.addFileData.uploading ||
                                  $scope.addFileData.recipientEmails.length === 0 ||
                                  $scope.addFileData.ccRecipientEmails.length === 0 ||
                                  $scope.addFileData.subject === undefined ||
                                  $scope.addFileData.subject === "" ||
                                  $scope.addFileData.message === undefined ||
                                  $scope.addFileData.message === "";
                    } else {
                        disable = $scope.addFileData.files === undefined ||
                                  $scope.addFileData.uploading ||
                                  $scope.addFileData.recipientEmails.length === 0 ||
                                  $scope.addFileData.subject === undefined ||
                                  $scope.addFileData.subject === "" ||
                                  $scope.addFileData.message === undefined ||
                                  $scope.addFileData.message === "";
                    }
                } else {
                    disable = $scope.addFileData.files === undefined ||
                              $scope.addFileData.uploading;
                }
            } else {
                if ($scope.addFileData.sendEmailToRecipient) {
                    if ($scope.addFileData.sendEmailToCCRecipient) {
                        disable = $scope.addFileData.files === undefined ||
                                  $scope.addFileData.fieldPredicate == undefined ||
                                  $scope.addFileData.uploading ||
                                  $scope.addFileData.recipientEmails.length === 0 ||
                                  $scope.addFileData.ccRecipientEmails.length === 0 ||
                                  $scope.addFileData.subject === undefined ||
                                  $scope.addFileData.subject === "" ||
                                  $scope.addFileData.message === undefined ||
                                  $scope.addFileData.message === "";
                    } else {
                        disable = $scope.addFileData.files === undefined ||
                                  $scope.addFileData.fieldPredicate == undefined ||
                                  $scope.addFileData.uploading ||
                                  $scope.addFileData.recipientEmails.length === 0 ||
                                  $scope.addFileData.subject === undefined ||
                                  $scope.addFileData.subject === "" ||
                                  $scope.addFileData.message === undefined ||
                                  $scope.addFileData.message === "";
                    }
                } else {
                    disable = $scope.addFileData.files === undefined ||
                              $scope.addFileData.fieldPredicate == undefined ||
                              $scope.addFileData.uploading;
                }
            }
            return disable;
        };

        $scope.activeDocumentBox = {
            "title": "Active Document",
            "viewUrl": "views/sideboxes/activeDocument.html",
            "getPrimaryDocumentFileName": primaryDocumentFileName,
            "downloadPrimaryDocument": function () {
                $scope.getFile($scope.submission.primaryDocumentFieldValue);
            },
            "uploadNewFile": function () {
                $scope.errorMessage = "";
                $scope.openModal('#addFileModal');
            },
            "gotoAllFiles": function () {
                $location.hash('all-files');
                $anchorScroll();
            },
            "hasPrimaryDocument": $scope.hasPrimaryDocument
        };

        var getLastActionDate = function() {
            var index = $scope.submission.actionLogs.length - 1;
            return $scope.submission.actionLogs[index].actionDate;
        };

        var getLastActionEntry = function() {
            var index = $scope.submission.actionLogs.length - 1;
            return $scope.submission.actionLogs[index].entry;
        };

        var isUmiRelease = function() {
            var umiRelease = 'no';
            for (var i in $scope.submission.fieldValues) {
                var fv = $scope.submission.fieldValues[i];
                if(fv.fieldPredicate.value === "umi_publication") {
                    umiRelease = fv.value === 'true' ? 'yes' : 'no';
                    break;
                }
            }
            return umiRelease;
        };

        $scope.submissionStatusBox = {
            "isUmiRelease": isUmiRelease,
            "getLastActionDate": getLastActionDate,
            "getLastActionEntry": getLastActionEntry,
            "depositLocations": depositLocations,
            "title": "Submission Status",
            "viewUrl": "views/sideboxes/submissionStatus.html",
            "submission": $scope.submission,
            "SubmissionStatusRepo": SubmissionStatusRepo,
            "submissionStatuses": submissionStatuses,
            "advanced": true,
            "assignableUsers": UserRepo.getAssignableUsers(),
            "user": UserService.getCurrentUser(),
            "sending": false,
            "sendAdvisorEmail": function () {
                $scope.submissionStatusBox.sending = true;
                $scope.submission.sendAdvisorEmail().then(function () {
                    $scope.submissionStatusBox.sending = false;
                    $scope.closeModal();
                });
            },
            "cancelStatus": SubmissionStatusRepo.findByName('Cancelled'),
            "changeStatus": function (state) {
                $scope.submissionStatusBox.updating = true;
                state.updating = true;
                $scope.submission.changeStatus(state.name).then(function (response) {
                    delete state.updating;
                    delete $scope.submissionStatusBox.updating;
                    $scope.submissionStatusBox.resetStatus();
                });
            },
            "publish": function (state) {
                $scope.submissionStatusBox.updating = true;
                state.updating = true;
                $scope.submission.publish($scope.submissionStatusBox.depositLocation).then(function () {
                    delete state.updating;
                    delete $scope.submissionStatusBox.updating;
                    $scope.submissionStatusBox.resetStatus();
                });
            },
            "deleteSubmission": function () {
                $scope.submission.delete().then(function () {
                    $scope.submissionStatusBox.deleteWorking = false;
                    $location.path("/admin/list");
                });
            },
            "changeAssignee": function (assignee) {
                $scope.submission.assign(assignee).then(function () {
                    $scope.submissionStatusBox.resetStatus();
                    $scope.submissionStatusBox.resetAssigneeWorking();
                });
            },
            "resetStatus": function () {
                $scope.submissionStatusBox.advanced = true;
                $scope.submissionStatusBox.newStatus = submissionStatuses[0];
                $scope.closeModal();
            },
            "resetAssigneeWorking": function () {
                $scope.submissionStatusBox.assignSaveWorking = false;
                $scope.submissionStatusBox.unassignWorking = false;
                $scope.submissionStatusBox.assignWorking = false;
            },
            "setSubmitDate": function (newDate) {
                $scope.submissionStatusBox.savingDate = true;
                $scope.submission.setSubmissionDate(newDate).then(function () {
                    $scope.submissionStatusBox.savingDate = false;
                });
            }
        };

        $scope.customActionsBox = {
            "title": "Custom Actions",
            "viewUrl": "views/sideboxes/customActions.html",
            "submission": $scope.submission,
            "updateCustomActionValue": function (cav) {
                $scope.submission.updateCustomActionValue(cav);
            }
        };

        $scope.flaggedFieldProfilesBox = {
            "title": "Flagged Fields",
            "viewUrl": "views/sideboxes/flaggedFieldProfiles.html",
            "submission": $scope.submission
        };

        $scope.showVocabularyWord = function (vocabularyWord, fieldProfile) {
            var result = true;

            if (angular.isDefined(fieldProfile) && angular.isDefined(fieldProfile.fieldPredicate)) {
                if (fieldProfile.fieldPredicate.value === "proquest_embargos" || fieldProfile.fieldPredicate.value === "default_embargos") {
                    var selectedValue;

                    // Always make the currently selected value visible, even if isActive is FALSE.
                    angular.forEach($scope.submission.fieldValues, function(fieldValue) {
                        if (fieldValue.fieldPredicate.id === fieldProfile.fieldPredicate.id) {
                            selectedValue = fieldValue.value;
                            return;
                        }
                    });

                    angular.forEach($scope.embargoes, function(embargo) {
                        if (Number(vocabularyWord.identifier) === embargo.id) {
                            if (angular.isDefined(selectedValue) && embargo.name === selectedValue) {
                                result = true;
                            } else {
                                result = embargo.isActive;
                            }

                            return;
                        }
                    });
                }
            }

            return result;
        };

        SidebarService.addBoxes([$scope.activeDocumentBox, $scope.submissionStatusBox, $scope.flaggedFieldProfilesBox, $scope.customActionsBox]);

    }).catch(function(errorMessage) {
        // handle errors
        console.log(errorMessage);
        $location.path("/admin/viewError");
    });

});
