vireo.directive("actionlog", function($timeout, NgTableParams) {
    return {
        templateUrl: "views/directives/actionLog.html",
        restrict: 'E',
        scope: {
            'submission': '=',
            'public': '=',
            'delay': '@'
        },
        link: function($scope) {

            $scope.tableParams = new NgTableParams({
                sorting: {
                    id: "desc"
                }
            }, {
                counts: [],
                dataset: $scope.submission.actionLogs
            });

            if (angular.isDefined($scope.submission.actionLogListenPromise)) {
                $scope.submission.actionLogListenPromise.then(null, null, function() {
                    if (angular.isUndefined($scope.debounce)) {
                        $scope.debounce = function() {
                            $scope.tableParams.reload();

                            // Do not use "null" because isUndefined() does not trigger for null.
                            $scope.debounce = undefined;
                        };

                        $timeout($scope.debounce, $scope.delay);
                    }
                });
            }
        }
    };
});
