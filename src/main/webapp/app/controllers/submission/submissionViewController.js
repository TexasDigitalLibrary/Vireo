vireo.controller("SubmissionViewController", function ($controller, $filter, $q, $scope, $routeParams, FieldPredicateRepo, FileUploadService, StudentSubmissionRepo, StudentSubmission, SubmissionStates) {

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

        StudentSubmissionRepo.findSubmissionById($routeParams.submissionId).then(function (submission) {
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
            FileUploadService.archiveFile($scope.submission, $scope.submission.primaryDocumentFieldValue).then(function () {
                $scope.archivingManuscript = false;
                $scope.submission.addFieldValue($scope.submission.getPrimaryDocumentFieldProfile().fieldPredicate);
            });
        };

        $scope.removibleDocuments = function(fieldValue) {
            return fieldValue.id && fieldValue.fieldPredicate.documentTypePredicate && fieldValue.fieldPredicate.value !== '_doctype_primary' && fieldValue.fieldPredicate.value !== '_doctype_license';
        };

        $scope.uploadableFieldPredicates = function(fieldPredicate) {
            return fieldPredicate.documentTypePredicate && fieldPredicate.value !== '_doctype_primary' && fieldPredicate.value !== '_doctype_license';
        };

    });

});
