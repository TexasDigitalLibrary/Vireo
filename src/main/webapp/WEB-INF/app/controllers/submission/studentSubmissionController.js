vireo.controller("StudentSubmissionController", function($controller, $scope, $location, $routeParams, $anchorScroll, $timeout, StudentSubmissionRepo, StudentSubmission, FieldValue) {

    angular.extend(this, $controller('AbstractController', {$scope: $scope}));

    $scope.studentSubmissionRepoReady = false;

    StudentSubmissionRepo.findSubmissionById($routeParams.submissionId).then(function(data) {

        $timeout(function() {
            $anchorScroll();
        });

        $scope.studentSubmissionRepoReady = true;
        $scope.submission = new StudentSubmission(angular.fromJson(data.body).payload.Submission);

        if ($location.hash()) {
            $scope.submission.ready().then(function() {
                $scope.submission.validate();
            });
        }

        $scope.onLastStep = function() {
            var currentStepIndex = $scope.submission.submissionWorkflowSteps.indexOf($scope.nextStep);
            return currentStepIndex === -1;
        };

        var currentStep = $routeParams.stepNum
            ? $scope.submission.submissionWorkflowSteps[$routeParams.stepNum - 1]
            : $scope.submission.submissionWorkflowSteps[0];

        $scope.setActiveStep(currentStep);

    });

    $scope.setActiveStep = function(step, hash) {

        var stepIndex = $scope.submission.submissionWorkflowSteps.indexOf(step);
        var reviewStepNum = $scope.submission.submissionWorkflowSteps.length + 1;
        var stepNum = stepIndex + 1;

        if (!step) {
            if (parseInt($routeParams.stepNum) === reviewStepNum) {
                step = {
                    name: "review"
                };
                stepNum = reviewStepNum;
            } else {
                stepIndex = 0;
                stepNum = stepIndex + 1;
                step = $scope.submission.submissionWorkflowSteps[stepIndex];
            }
        } else if (step.name === "review") {
            stepNum = reviewStepNum;
        }

        $scope.nextStep = $scope.submission.submissionWorkflowSteps[stepNum];
        $scope.activeStep = step;

        var nextLocation = "submission/" + $scope.submission.id + "/step/" + stepNum;

        if (hash) {
            nextLocation += "#" + hash;
        }

        // Only change path if it differs from the current path.
        if ("/" + nextLocation !== $location.path()) {
            $location.path(nextLocation, false);
        }

    };

    $scope.submit = function() {
        $scope.submitting = true;
        $scope.submission.submit().then(function() {
            $scope.submitting = false;
            $location.path("/submission/complete");
        });
    };

    $scope.reviewSubmission = function() {
        $scope.setActiveStep({name: 'review'});
    }

});
