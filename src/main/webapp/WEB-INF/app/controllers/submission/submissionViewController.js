vireo.controller("SubmissionViewController", function($controller, $filter, $q, $scope, $routeParams, FieldPredicateRepo, FileUploadService, StudentSubmissionRepo, StudentSubmission, SubmissionStates) {

    angular.extend(this, $controller('AbstractController', {$scope: $scope}));

    $scope.fieldPredicate = {};

    $scope.fieldPredicates = FieldPredicateRepo.getAll();

    $scope.SubmissionStates = SubmissionStates;

    StudentSubmissionRepo.findSubmissionById($routeParams.submissionId).then(function(submission) {
        $scope.loaded = true;
        $scope.submission = new StudentSubmission(submission);
    });

    var deleteFieldValue = function(fieldValue) {
        fieldValue.removing = true;
        FileUploadService.removeFile($scope.submission, fieldValue).then(function() {
            delete fieldValue.removing;
        });
    };

    $scope.updateActionLogLimit = function() {
        $scope.actionLogCurrentLimit = $scope.actionLogCurrentLimit === $scope.actionLogLimit ? $scope.submission.actionLogs.length : $scope.actionLogLimit;
    };

    $scope.message = '';

    $scope.addMessage = function() {
        $scope.messaging = true;
        $scope.submission.addMessage($scope.message).then(function() {
            $scope.messaging = false;
            $scope.message = '';
        });
    };

    $scope.getFileType = function(fieldPredicate) {
        return FileUploadService.getFileType(fieldPredicate);
    };

    $scope.isPrimaryDocument = function(fieldPredicate) {
        return $scope.getFileType(fieldPredicate) == 'PRIMARY';
    };

    $scope.removeQueue = [];

    $scope.removingUploads = false;

    $scope.archivingManuscript = false;

    $scope.queueRemove = function(fieldValue) {
        var index = $scope.removeQueue.indexOf(fieldValue);
        if (index >= 0) {
            $scope.removeQueue.splice(index, 1);
        } else {
            $scope.removeQueue.push(fieldValue);
        }
    };

    $scope.removeAdditionalUploads = function() {
        $scope.removingUploads = true;
        var removePromises = [];
        for (var i in $scope.removeQueue) {
            var fieldValue = $scope.removeQueue[i];
            removePromises.push(deleteFieldValue(fieldValue));
        }
        $q.all(removePromises).then(function() {
            $scope.removingUploads = false;
        });
    };

    $scope.cancelUpload = function() {
        $scope.removeQueue = [];
        $scope.removingUploads = false;
        $scope.closeModal();
    };

    $scope.archiveManuscript = function() {
        $scope.archivingManuscript = true;
        FileUploadService.archiveFile($scope.submission, $scope.submission.primaryDocumentFieldValue).then(function() {
            $scope.archivingManuscript = false;
            $scope.submission.addFieldValue($scope.submission.getPrimaryDocumentFieldProfile().fieldPredicate);
        });
    };

});
