vireo.controller("StudentSubmissionController", function ($controller, $scope, $location, $routeParams, $anchorScroll, $timeout, EmbargoRepo, StudentSubmissionRepo, StudentSubmission, ManagedConfigurationRepo) {

    angular.extend(this, $controller('AbstractController', {
        $scope: $scope
    }));

    $scope.studentSubmissionRepoReady = false;

    $scope.configuration = ManagedConfigurationRepo.getAll();

    $scope.embargoes = EmbargoRepo.getAll();

    StudentSubmissionRepo.fetchSubmissionById($routeParams.submissionId).then(function (submission) {

        $scope.studentSubmissionRepoReady = true;

        $scope.submission = submission;

        $scope.submission.fetchDocumentTypeFileInfo();

        $scope.onLastStep = function () {
            var currentStepIndex = $scope.submission.submissionWorkflowSteps.indexOf($scope.nextStep);
            return currentStepIndex === -1;
        };

        if (angular.isDefined($scope.submission.submissionWorkflowSteps) && $scope.submission.submissionWorkflowSteps !== null) {
            var currentStep = $routeParams.stepNum ? $scope.submission.submissionWorkflowSteps[$routeParams.stepNum - 1] : $scope.submission.submissionWorkflowSteps[0];

            $scope.setActiveStep(currentStep);
        }
    });

    $scope.setActiveStep = function (step, hash) {
        if ($scope.submitting) {
            // do not allow changing the active step while submitting to prevent changing field values.
            return;
        }

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
            $scope.submission.validate();
        }

        $scope.nextStep = $scope.submission.submissionWorkflowSteps[stepNum];
        $scope.activeStep = step;

        var nextLocation = "submission/" + $scope.submission.id + "/step/" + stepNum;

        if (hash) {
            nextLocation += "#" + hash;
        }

        // Only change path if it differs from the current path.
        if ("/" + nextLocation !== $location.path()) {
            $location.path(nextLocation);
        }

        $timeout(function () {
            $anchorScroll();
        });

    };

    $scope.submit = function () {
        $scope.submitting = true;
        $scope.submission.submit().then(function () {
            $scope.submitting = false;
            $location.path("/submission/complete");
        });
    };

    $scope.reviewSubmission = function () {
        $scope.setActiveStep({
            name: 'review'
        });
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

    $scope.sortEmbargos = function(word) {
        var embargo = null;
        angular.forEach($scope.embargoes, function(potentialEmbargo) {
            if (Number(word.identifier) === potentialEmbargo.id) {
                embargo = potentialEmbargo;
            }
        });
        return embargo.position;
    };
});
