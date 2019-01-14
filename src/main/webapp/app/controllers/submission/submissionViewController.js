vireo.controller("SubmissionViewController", function ($controller, $q, $scope, $routeParams, CustomActionDefinitionRepo, FieldPredicateRepo, FileUploadService, StudentSubmissionRepo, SubmissionStates) {

    angular.extend(this, $controller('AbstractController', {
        $scope: $scope
    }));

    $scope.SubmissionStates = SubmissionStates;

    $scope.fieldPredicates = FieldPredicateRepo.getAll();

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
            '_doctype_archived'
        ];

        $scope.removableDocuments = function(fieldValue) {
            return fieldValue.id && fieldValue.fieldPredicate.documentTypePredicate && protectedDocTypes.indexOf(fieldValue.fieldPredicate.value) < 0;
        };

        $scope.uploadableFieldPredicates = function(fieldPredicate) {
            return fieldPredicate.documentTypePredicate && protectedDocTypes.indexOf(fieldPredicate.value) < 0;
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
