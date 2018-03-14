vireo.controller("AdminSubmissionViewController", function ($anchorScroll, $controller, $location, $q, $routeParams, $scope, DepositLocationRepo, EmailTemplateRepo, FieldPredicateRepo, FieldValue, FileUploadService, SidebarService, SubmissionRepo, SubmissionStatusRepo, UserRepo, UserService, UserSettings, SubmissionStatuses) {

    angular.extend(this, $controller('AbstractController', {
        $scope: $scope
    }));

    $scope.updateActionLogLimit = function () {
        $scope.actionLogCurrentLimit = $scope.actionLogCurrentLimit === $scope.actionLogLimit ? $scope.submission.actionLogs.length : $scope.actionLogLimit;
    };

    $scope.fieldPredicates = FieldPredicateRepo.getAll();

    var userSettings = new UserSettings();

    var submissionStatuses = SubmissionStatusRepo.getAll();

    var depositLocations = DepositLocationRepo.getAll();

    var emailTemplates = EmailTemplateRepo.getAll();

    EmailTemplateRepo.ready().then(function() {

        var addDefaultTemplate = true;
        for (var i in emailTemplates) {
            var template = emailTemplates[i];
            if (template.name === "Choose a Message Template") {
                addDefaultTemplate = false;
                break;
            }
        }

        if (addDefaultTemplate) {
            emailTemplates.unshift({
                name: "Choose a Message Template"
            });
        }

    });

    var initializeEmailRecipients = function() {
        $scope.recipientEmails = [];
        $scope.ccRecipientEmails = [];
    };

    initializeEmailRecipients();

    $scope.loaded = true;

    $scope.addCommentModal = {};

    SubmissionRepo.findSubmissionById($routeParams.id).then(function(submission) {

        $scope.submission = submission;

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
            addCommentModal.commentVisiblity = userSettings.notes_mark_comment_as_private_by_default ? "private" : "public";
            addCommentModal.recipientEmail = userSettings.notes_email_student_by_default === "true" ? $scope.submission.submitter.email : "";
            addCommentModal.ccRecipientEmail = userSettings.notes_cc_student_advisor_by_default === "true" ? $scope.submission.getContactEmails().join(",") : "";
            addCommentModal.sendEmailToRecipient = (userSettings.notes_email_student_by_default === "true") || (userSettings.notes_cc_student_advisor_by_default === "true");
            addCommentModal.sendEmailToCCRecipient = userSettings.notes_cc_student_advisor_by_default === "true";
            addCommentModal.subject = "";
            addCommentModal.message = "";
            addCommentModal.actionLogCurrentLimit = $scope.actionLogLimit;
            addCommentModal.selectedTemplate = emailTemplates[0];
            addCommentModal.needsCorrection = userSettings.notes_flag_submission_as_needs_corrections_by_default === "true";
        };

        $scope.addComment = function (addCommentModal) {
            addCommentModal.adding = true;
            $scope.submission.addComment(addCommentModal).then(function () {
                if (addCommentModal.needsCorrection) {
                    $scope.submission.changeStatus(SubmissionStatuses.NEEDS_CORRECTIONS);
                }
                $scope.resetCommentModal(addCommentModal);
            });
        };

        $scope.resetCommentModal($scope.addCommentModal);

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

        var resetFileData = function () {
            initializeEmailRecipients();
            $scope.addFileData = {
                selectedTemplate: emailTemplates[0],
                sendEmailToRecipient: (userSettings.attachment_email_student_by_default === "true") || (userSettings.attachment_cc_student_advisor_by_default === "true"),
                recipientEmail: userSettings.attachment_email_student_by_default === "true" ? $scope.submission.submitter.email : "",
                sendEmailToCCRecipient: userSettings.attachment_cc_student_advisor_by_default === "true",
                ccRecipientEmail: userSettings.attachment_cc_student_advisor_by_default === "true" ? $scope.submission.getContactEmails().join(",") : "",
                needsCorrection: userSettings.attachment_flag_submission_as_needs_corrections_by_default === "true"
            };
        };

        resetFileData();

        $scope.queueUpload = function (files) {
            $scope.addFileData.files = files;
        };

        $scope.removeFiles = function () {
            delete $scope.addFileData.files;
        };

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

                var fieldProfile = $scope.submission.getFieldProfileByPredicate(fieldValue.fieldPredicate);

                if ($scope.addFileData.addFileSelection === 'replace' && $scope.hasPrimaryDocument()) {
                    FileUploadService.archiveFile($scope.submission, $scope.submission.primaryDocumentFieldValue);
                }

                fieldValue.value = response.data.meta.message;

                $scope.submission.saveFieldValue(fieldValue, fieldProfile).then(function (response) {
                    var apiRes = angular.fromJson(response.body);
                    if (apiRes.meta.status === "INVALID") {
                        fieldValue.refresh();
                    } else {
                        if ($scope.addFileData.sendEmailToRecipient) {
                            $scope.submission.sendEmail({
                                subject: $scope.addFileData.subject,
                                message: $scope.addFileData.message,
                                recipientEmail: $scope.recipientEmails.join(';'),
                                ccRecipientEmail: $scope.ccRecipientEmails.join(';'),
                                sendEmailToRecipient: $scope.addFileData.sendEmailToRecipient,
                                sendEmailToCCRecipient: $scope.addFileData.sendEmailToCCRecipient
                            }).then(function () {
                                $scope.resetAddFile();
                            });
                        } else {
                            $scope.resetAddFile();
                        }
                    }
                });

            }, function (response) {
                console.log('Error status: ' + response.status);
            }, function (progress) {
                $scope.addFileData.progress = progress;
            });

            if ($scope.addFileData.needsCorrection) {
                $scope.submission.needsCorrection();
            }

        };

        $scope.resetAddFile = function () {
            resetFileData();
            $scope.closeModal();
        };

        $scope.disableSubmitAddFile = function () {
            var disable = true;
            if ($scope.addFileData.addFileSelection == 'replace') {
                disable = $scope.addFileData.files === undefined || $scope.addFileData.uploading;
            } else {
                disable = $scope.addFileData.files === undefined || $scope.addFileData.fieldPredicate === undefined || $scope.addFileData.uploading;
            }
            return disable;
        };

        $scope.resetAddCommentModal = function () {
            $scope.closeModal();
        };

        $scope.addEmailAddressee = function (emailAddress,destinationModel) {
            if (emailAddress) {
                destinationModel.push(emailAddress);
            }
        };

        $scope.removeEmailAddressee = function (email,destinationModel) {
            var removeIndex = destinationModel.indexOf(email);
            destinationModel.splice(removeIndex,1);
        };

        $scope.activeDocumentBox = {
            "title": "Active Document",
            "viewUrl": "views/sideboxes/activeDocument.html",
            "getPrimaryDocumentFileName": primaryDocumentFileName,
            "downloadPrimaryDocument": function () {
                $scope.getFile($scope.submission.primaryDocumentFieldValue);
            },
            "uploadNewFile": function () {
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
            var umiReleaseFilterFieldValue = $scope.submission.fieldValues.filter(function (fv) {
                if(fv.fieldPredicate.value === "umi_publication") {
                    umiRelease = fv.value === 'true' ? 'yes' : 'no';
                    break;
                }
            });
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
                    var apiRes = angular.fromJson(response.body);
                    if(apiRes.meta.status === 'SUCCESS') {
                        // remove field values that are document type field predicates
                        for(var i = $scope.submission.fieldValues.length - 1; i >= 0; i--) {
                            var fieldValue = $scope.submission.fieldValues[i];
                            if(fieldValue.fieldPredicate.documentTypePredicate) {
                                $scope.submission.fieldValues.splice(i, 1);
                            }
                        }
                        // add field values of response that are document type field predicates
                        var submission = apiRes.payload.Submission;
                        angular.forEach(submission.fieldValues, function (fieldValue) {
                            if(fieldValue.fieldPredicate.documentTypePredicate) {
                                $scope.submission.fieldValues.push(new FieldValue(fieldValue));
                            }
                        });
                        // update current submissions status
                        angular.extend($scope.submission.submissionStatus, submission.submissionStatus);
                        // fetch file info
                        $scope.submission.fetchDocumentTypeFileInfo();
                    }
                    delete state.updating;
                    delete $scope.submissionStatusBox.updating;
                    $scope.submissionStatusBox.resetStatus();
                });
            },
            "publish": function (state) {
                $scope.submissionStatusBox.updating = true;
                state.updating = true;
                $scope.submission.publish($scope.submissionStatusBox.depositLocation).then(function (response) {
                    var apiRes = angular.fromJson(response.body);
                    if(apiRes.meta.status === 'SUCCESS') {
                        var submission = apiRes.payload.Submission;
                        angular.extend($scope.submission.submissionStatus, submission.submissionStatus);
                    }
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
                });
            },
            "resetStatus": function () {
                $scope.submissionStatusBox.advanced = true;
                $scope.submissionStatusBox.newStatus = submissionStatuses[0];
                $scope.closeModal();
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

        SidebarService.addBoxes([$scope.activeDocumentBox, $scope.submissionStatusBox, $scope.customActionsBox, $scope.flaggedFieldProfilesBox]);

    });

});
