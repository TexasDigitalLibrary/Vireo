vireo.controller("AdminSubmissionViewController", function ($anchorScroll, $controller, $location, $q, $routeParams, $scope, DepositLocationRepo, EmailTemplateRepo, FieldPredicateRepo, FieldValue, FileUploadService, SidebarService, SubmissionRepo, SubmissionStatusRepo, UserRepo, User, UserSettings, SubmissionStatuses) {

    angular.extend(this, $controller('AbstractController', {
        $scope: $scope
    }));

    $scope.updateActionLogLimit = function () {
        $scope.actionLogCurrentLimit = $scope.actionLogCurrentLimit === $scope.actionLogLimit ? $scope.submission.actionLogs.length : $scope.actionLogLimit;
    };

    var userSettingsUnfetched = new UserSettings();
    userSettingsUnfetched.fetch();

    var ready = $q.all([
        SubmissionRepo.findSubmissionById($routeParams.id),
        UserRepo.getAll(),
        SubmissionStatusRepo.getAll(),
        EmailTemplateRepo.getAll(),
        FieldPredicateRepo.getAll(),
        DepositLocationRepo.getAll(),
        userSettingsUnfetched.ready()
    ]);

    ready.then(function (resolved) {

        var submission = resolved[0];
        var users = resolved[1];
        var submissionStatuses = resolved[2];
        var emailTemplates = resolved[3];
        var fieldPredicates = resolved[4];
        var depositLocations = resolved[5];
        var userSettings = resolved[6];

        $scope.loaded = true;

        $scope.submission = submission;
        $scope.submission.fetchDocumentTypeFileInfo();

        $scope.emailTemplates = emailTemplates;

        $scope.fieldPredicates = fieldPredicates;

        var firstName = $scope.submission.submitter.firstName;
        var lastName = $scope.submission.submitter.lastName;
        var organization = $scope.submission.organization;

        var firstAssignable = function () {
            var firstAssignable;
            for (var i in users) {
                if (users[i].role === "ROLE_ADMIN" || users[i].role === "ROLE_MANAGER") {
                    firstAssignable = users[i];
                    break;
                }
            }
            return firstAssignable;
        };

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

        var hasPrimaryDocumentFieldValue = function () {
            return $scope.submission.primaryDocumentFieldValue !== undefined && $scope.submission.primaryDocumentFieldValue !== null;
        };

        $scope.addCommentModal = {};

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

        $scope.title = lastName + ', ' + firstName + ' (' + organization.name + ')';

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

        $scope.deleteFieldValue = function (fieldValue) {
            fieldValue.updating = true;
            FileUploadService.removeFile($scope.submission, fieldValue).then(function () {
                $scope.closeModal();
                $scope.confirm = false;
                delete fieldValue.updating;
            });
        };

        $scope.saveFieldValue = function (fieldValue) {
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

            var fieldValue = $scope.addFileData.addFileSelection === 'replace' ? $scope.submission.primaryDocumentFieldValue : new FieldValue({
                fieldPredicate: $scope.addFileData.fieldPredicate
            });

            fieldValue.file = $scope.addFileData.files[0];

            FileUploadService.uploadFile($scope.submission, fieldValue).then(function (response) {

                var fieldProfile = $scope.submission.getFieldProfileByPredicate(fieldValue.fieldPredicate);

                if ($scope.addFileData.addFileSelection === 'replace') {
                    $scope.submission.archiveFile($scope.submission.primaryDocumentFieldValue).then(function (response) {
                        var archivedDocumentFieldValue = new FieldValue();

                        archivedDocumentFieldValue.value = angular.fromJson(response.body).meta.message;

                        var archivedDocumentFieldProfile = $scope.submission.getFieldProfileByPredicateName("_doctype_archived");

                        if (archivedDocumentFieldProfile !== undefined && archivedDocumentFieldProfile !== null) {
                            archivedDocumentFieldValue.fieldPredicate = archivedDocumentFieldProfile.fieldPredicate;

                            archivedDocumentFieldValue.updating = true;

                            $scope.submission.saveFieldValue(archivedDocumentFieldValue, archivedDocumentFieldProfile).then(function (response) {
                                if (angular.fromJson(response.body).meta.status === "INVALID") {
                                    fieldValue.refresh();
                                }
                                archivedDocumentFieldValue.updating = false;
                            });
                        } else {
                            console.warn("No archived field profile exists on submission!");
                        }
                    });
                }

                fieldValue.value = response.data.meta.message;

                $scope.submission.saveFieldValue(fieldValue, fieldProfile).then(function (response) {
                    if (angular.fromJson(response.body).meta.status === "INVALID") {
                        fieldValue.refresh();
                    } else {
                        if ($scope.addFileData.sendEmailToRecipient) {
                            $scope.submission.sendEmail({
                                subject: $scope.addFileData.subject,
                                message: $scope.addFileData.message,
                                recipientEmail: $scope.addFileData.recipientEmail,
                                ccRecipientEmail: $scope.addFileData.ccRecipientEmail,
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

        $scope.hasPrimaryDocument = function () {
            return hasPrimaryDocumentFieldValue() && $scope.submission.primaryDocumentFieldValue.id !== undefined;
        };

        $scope.activeDocumentBox = {
            "title": "Active Document",
            "viewUrl": "views/sideboxes/activeDocument.html",
            "getPrimaryDocumentFileName": function () {
                return hasPrimaryDocumentFieldValue() ? $scope.submission.primaryDocumentFieldValue.fileInfo !== undefined ? $scope.submission.primaryDocumentFieldValue.fileInfo.name : '' : '';
            },
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

        $scope.submissionStatusBox = {
            "newStatus": submissionStatuses[0],
            "depositLocations": depositLocations,
            "title": "Submission Status",
            "viewUrl": "views/sideboxes/submissionStatus.html",
            "submission": $scope.submission,
            "SubmissionStatusRepo": SubmissionStatusRepo,
            "submissionStatuses": submissionStatuses,
            "advanced": true,
            "allUsers": UserRepo.getAll(),
            "user": new User(),
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
                $scope.submission.changeStatus(state.name).then(function () {
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
                });
            },
            "assignee": firstAssignable(),
            "resetStatus": function () {
                $scope.submissionStatusBox.advanced = true;
                $scope.submissionStatusBox.newStatus = submissionStatuses[0];
                $scope.submissionStatusBox.assignee = firstAssignable();
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
