vireo.directive("actionlog", function() {
    return {
        templateUrl: "views/directives/actionLog.html",
        restrict: 'E',
        scope: {
            'submission': '=',
            'public': '='
        },
        link: function($scope, element, attr) {}
    }
});
