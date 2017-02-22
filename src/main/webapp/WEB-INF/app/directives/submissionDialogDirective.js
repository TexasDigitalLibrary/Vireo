vireo.directive("submissiondialog", function($location) {
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
            $scope.submitCorrections = function() {
                $scope.submittingCorrections = true;
                $scope.submission.submitCorrections().then(function(response) {
                    angular.extend($scope.submission, angular.fromJson(response.body).payload.Submission);
                    $location.path("/submission/complete");
                    $scope.submittingCorrections = false;
                });
            }
        }
    }
});
