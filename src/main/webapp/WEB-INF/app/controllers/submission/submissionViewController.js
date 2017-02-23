vireo.controller("SubmissionViewController", function($controller, $scope, $routeParams, StudentSubmissionRepo, StudentSubmission) {

    angular.extend(this, $controller('AbstractController', {$scope: $scope}));

    StudentSubmissionRepo.findSubmissionById($routeParams.submissionId).then(function(data) {
        $scope.loaded = true;
        $scope.submission = new StudentSubmission(angular.fromJson(data.body).payload.Submission);
    });

    $scope.message = '';

    $scope.addMessage = function() {
        $scope.messaging = true;
        $scope.submission.addMessage($scope.message).then(function() {
            $scope.messaging = false;
            $scope.message = '';
        });
    };

});
