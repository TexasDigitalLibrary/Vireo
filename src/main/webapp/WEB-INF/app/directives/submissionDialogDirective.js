vireo.directive("submissiondialog", function() {
    return {
        templateUrl: function(element, attr) {
            console.log("views/directives/submissionDialog-" + attr.type + ".html")
            return "views/directives/submissionDialog-" + attr.type + ".html"
        },
        scope: {
            submission: '='
        },
        link: function($scope, element, attr, parent) {
            $scope.show = [true, false];
        },
        controller: function($scope) {
            $scope.submitCorrections = function() {
                $scope.submission.submitCorrections().then(function(response) {
                    angular.extend($scope.submission, angular.fromJson(response.body).payload.Submission);
                });
            }
        }
    }
});
