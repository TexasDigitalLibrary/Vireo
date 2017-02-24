vireo.controller("SubmissionViewController", function($controller, $filter, $q, $scope, $routeParams, FileUploadService, StudentSubmissionRepo, StudentSubmission) {

    angular.extend(this, $controller('AbstractController', {$scope: $scope}));

    StudentSubmissionRepo.findSubmissionById($routeParams.submissionId).then(function(response) {
        $scope.loaded = true;
        $scope.submission = new StudentSubmission(angular.fromJson(response.body).payload.Submission);
    });

    var deleteFieldValue = function(fieldValue) {
        fieldValue.removing = true;
        FileUploadService.removeFile($scope.submission, fieldValue).then(function() {
            delete fieldValue.removing;
        });
    };

    $scope.actionLogLimit = 10;

    $scope.actionLogCurrentLimit = $scope.actionLogLimit;

    $scope.updateActionLogLimit = function() {
        $scope.actionLogCurrentLimit = $scope.actionLogCurrentLimit === $scope.actionLogLimit
            ? $scope.submission.actionLogs.length
            : $scope.actionLogLimit;
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

});
