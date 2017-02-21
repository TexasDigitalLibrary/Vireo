vireo.directive("submissiondialog", function() {
    return {
        templateUrl: "views/directives/submissionDialog.html",
        scope: {
            submission: '='
        },
        link: function($scope, element, attr, parent) {
            $scope.ac = ['-', '+'];
            $scope.toggle = function() {
                $scope.ac.reverse()
            }
        }
    }
});
