vireo.controller("SubmissionViewController", function ($controller, $q, $scope, $routeParams, CustomActionDefinitionRepo, EmbargoRepo, FieldPredicateRepo, FileUploadService, StudentSubmissionRepo, SubmissionStates) {

    angular.extend(this, $controller('AbstractController', {
        $scope: $scope
    }));

    $scope.SubmissionStates = SubmissionStates;

    $scope.fieldPredicates = FieldPredicateRepo.getAll();

    $scope.embargoes = EmbargoRepo.getAll();

    $scope.actionLogDelay = 2000;

    FieldPredicateRepo.ready().then(function() {

        var deleteFile = function (fieldValue) {
            fieldValue.removing = true;
            FileUploadService.removeFile($scope.submission, fieldValue).then(function (removed) {
                delete fieldValue.removing;
            });
        };

        StudentSubmissionRepo.fetchSubmissionById($routeParams.submissionId).then(function (submission) {
            $scope.loaded = true;
            $scope.submission = submission;
            $scope.submission.fetchDocumentTypeFileInfo();
        });

        $scope.message = '';

        $scope.removeQueue = [];

        $scope.removingUploads = false;

        $scope.archivingManuscript = false;

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
            $scope.actionLogCurrentLimit = $scope.actionLogCurrentLimit === $scope.actionLogLimit ? $scope.submission.actionLogs.length : $scope.actionLogLimit;
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
            $q.all(removePromises).then(function () {
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
            FileUploadService.archiveFile($scope.submission, $scope.submission.primaryDocumentFieldValue, true).then(function () {
                $scope.archivingManuscript = false;
                $scope.submission.addFieldValue($scope.submission.getPrimaryDocumentFieldProfile().fieldPredicate);
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
                StudentSubmissionRepo.remove($scope.submission);
                StudentSubmissionRepo.fetchSubmissionById($routeParams.submissionId).then(function (submission) {
                    angular.extend($scope.submission, submission);
                });
            }
        });

    });

});
