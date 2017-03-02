vireo.directive("actionlog", function(NgTableParams) {
    return {
        templateUrl: "views/directives/actionLog.html",
        restrict: 'E',
        scope: {
            'submission': '=',
            'public': '='
        },
        link: function($scope) {

            console.log(ngIfDirective);

            $scope.tableParams = new NgTableParams({
                sorting: {id: "desc"},
            }, {
                counts: [],
                dataset: $scope.submission.actionLogs
            });

            $scope.submission.actionLogListenPromise.then(null, null, function() {
                $scope.tableParams.reload();
            });
        }
    }
});
