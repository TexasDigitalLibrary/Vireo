vireo.directive("actionlog", function() {
    return {
        templateUrl: "views/directives/actionLog.html",
        restrict: 'E',
        scope: {
            'submission': '='
        },
        link: function($scope, element, attr) {

        }
    }
});
