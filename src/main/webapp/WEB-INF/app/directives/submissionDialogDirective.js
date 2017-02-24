vireo.directive("submissiondialog", function($anchorScroll, $location) {
    return {
        templateUrl: function(element, attr) {
            return "views/directives/submissionDialog-" + attr.type + ".html"
        },
        scope: {
            submission: '='
        },
        link: function($scope, element, attr, parent) {
            $scope.show = ['-', '+'];
        },
        controller: function($scope) {
            $scope.submitGloss = ["Submit Corrections", "Are you sure?"];

            $scope.submittingCorrections = false;

            $scope.submitCorrections = function() {
                if ($scope.submitGloss[0] === "Are you sure?") {
                    $scope.submittingCorrections = true;
                    $scope.submission.submitCorrections().then(function(response) {
                        angular.extend($scope.submission, angular.fromJson(response.body).payload.Submission);
                        $location.path("/submission/complete");
                        $scope.submittingCorrections = false;
                        $scope.submitGloss.reverse();
                    });
                } else {
                    $scope.submitGloss.reverse();
                }
            };

            $scope.jumpToUploadFiles = function() {
                $location.hash('upload-files');
                $anchorScroll();
            };
        }
    }
});
