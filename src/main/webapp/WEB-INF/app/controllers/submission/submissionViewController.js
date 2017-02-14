vireo.controller("SubmissionViewController", function ($controller, $scope, $routeParams, StudentSubmissionRepo, StudentSubmission) {

    angular.extend(this, $controller('AbstractController', {$scope: $scope}));

    $scope.studentSubmissionRepoReady = false;
    StudentSubmissionRepo.findSubmissionById($routeParams.submissionId).then(function(data) {
    	$scope.studentSubmissionRepoReady = true;
        $scope.submission = new StudentSubmission(angular.fromJson(data.body).payload.Submission);
    });

});
