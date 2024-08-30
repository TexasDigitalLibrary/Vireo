vireo.controller("SubmissionViewController", function ($controller, $q, $routeParams, $scope, $timeout, CustomActionDefinitionRepo, EmbargoRepo, FieldPredicateRepo, FileUploadService, StudentSubmissionRepo, SubmissionStates) {

    angular.extend(this, $controller('AbstractController', {
        $scope: $scope
    }));

    $scope.SubmissionStates = SubmissionStates;

    $scope.fieldPredicates = FieldPredicateRepo.getAll();

    $scope.embargoes = EmbargoRepo.getAll();

    $scope.actionLogDelay = 2000;

    $scope.fieldProfile = undefined;
    $scope.fieldPredicate = undefined;

    FieldPredicateRepo.ready().then(function() {

        var deleteFile = function (fieldValue) {
            fieldValue.removing = true;
            var deleteFilePromise = FileUploadService.removeFile($scope.submission, fieldValue);

            deleteFilePromise.then(function (removed) {
                if (removed) {
                    delete fieldValue.removing;
                }
            });

            return deleteFilePromise;
        };

        var fetchSubmission = function (refreshScope) {
            StudentSubmissionRepo.fetchSubmissionById($routeParams.submissionId).then(function (submission) {
                if (refreshScope) {
                    $scope.submission = submission;

                    $scope.fieldProfile = undefined;
                    $timeout(function () {
                        $scope.onSelectDocumentType($scope.fieldPredicate);
                    });
                } else {
                    angular.extend($scope.submission, submission);
                }
                $scope.submission.enableListeners();
                $scope.submission.fetchDocumentTypeFileInfo();
            });
        }

        fetchSubmission(true);

        $scope.message = '';

        $scope.removeQueue = [];

        $scope.removingUploads = false;

        $scope.archivingManuscript = false;

        $scope.onSelectDocumentType = function (fieldPredicate) {
            if (!!fieldPredicate && !!fieldPredicate.id) {
                $scope.fieldPredicate = fieldPredicate;
                $scope.fieldProfile = $scope.submission.getFieldProfileByPredicate(fieldPredicate);
            }
        };

        $scope.addMessage = function () {
            $scope.messaging = true;
            $scope.submission.addMessage($scope.message).then(function () {
                $scope.messaging = false;
                $scope.message = '';
            });
        };

        $scope.getFileType = function (fieldPredicate) {
            return FileUploadService.getFileType(fieldPredicate);
        };

        $scope.getFile = function (fieldValue) {
            $scope.submission.file(fieldValue.value).then(function (data) {
                saveAs(new Blob([data], {
                    type: fieldValue.fileInfo.type
                }), fieldValue.fileInfo.name);
            });
        };

        $scope.isPrimaryDocument = function (fieldPredicate) {
            return $scope.getFileType(fieldPredicate) == 'PRIMARY';
        };

        $scope.updateActionLogLimit = function () {
            if (angular.isDefined($scope.submission.actionLogs)) {
                $scope.actionLogCurrentLimit = $scope.actionLogCurrentLimit === $scope.actionLogLimit ? $scope.submission.actionLogs.length : $scope.actionLogLimit;
            } else {
                $scope.actionLogCurrentLimit = 0;
            }
        };

        $scope.queueRemove = function (fieldValue) {
            var index = $scope.removeQueue.indexOf(fieldValue);
            if (index >= 0) {
                $scope.removeQueue.splice(index, 1);
            } else {
                $scope.removeQueue.push(fieldValue);
            }
        };

        $scope.removeAdditionalUploads = function () {
            $scope.removingUploads = true;
            var removePromises = [];
            for (var i in $scope.removeQueue) {
                var fieldValue = $scope.removeQueue[i];
                removePromises.push(deleteFile(fieldValue));
            }
            $q.all(removePromises).then(function (removeFiles) {
                if (removeFiles.every(Boolean)) {
                    fetchSubmission(true);
                } else {
                    console.error('Failed to remove additional files. Please refresh the page.');
                }
                $scope.removeQueue = [];
                $scope.removingUploads = false;
            });
        };

        $scope.cancelUpload = function () {
            $scope.removeQueue = [];
            $scope.removingUploads = false;
            $scope.closeModal();
        };

        $scope.archiveManuscript = function () {
            $scope.archivingManuscript = true;
            FileUploadService.archiveFile($scope.submission, $scope.submission.primaryDocumentFieldValue, true).then(function (removeFieldValue) {
                $scope.archivingManuscript = false;
                if (removeFieldValue) {
                    fetchSubmission(true);
                } else {
                    console.error('Failed to remove field value. Please refresh the page.');
                }
            });
        };

        var protectedDocTypes = [
            '_doctype_primary',
            '_doctype_license',
            '_doctype_feedback',
            '_doctype_archived'
        ];

        $scope.removableDocuments = function(fieldValue) {
            return fieldValue.id && fieldValue.fieldPredicate.documentTypePredicate && protectedDocTypes.indexOf(fieldValue.fieldPredicate.value) < 0;
        };

        $scope.uploadableFieldPredicates = function(fieldPredicate) {
            return fieldPredicate.documentTypePredicate && protectedDocTypes.indexOf(fieldPredicate.value) < 0;
        };

        $scope.feedbackDocuments = function(fieldValue) {
            return fieldValue.id && fieldValue.fieldPredicate.documentTypePredicate && fieldValue.fieldPredicate.value == '_doctype_feedback';
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

        $scope.isEmbargo = function(fieldValue) {
            return (fieldValue.fieldPredicate.value=='default_embargos' || fieldValue.fieldPredicate.value=='proquest_embargos');
        };

        $scope.sortEmbargos= function(word) {
            var embargo = null;
            angular.forEach($scope.embargoes, function(potentialEmbargo) {
                if (Number(word.identifier) === potentialEmbargo.id) {
                    embargo = potentialEmbargo;
                }
            });
            return embargo.position;
        };

        CustomActionDefinitionRepo.listen(function(apiRes) {
            if(apiRes.meta.status === 'SUCCESS') {
                fetchSubmission();
            }
        });

        $scope.getPaginatedActionLog = function (orderBy, page, count) {
            return StudentSubmissionRepo.findPaginatedActionLogsById($routeParams.submissionId, orderBy, page, count);
        }

    });

});
