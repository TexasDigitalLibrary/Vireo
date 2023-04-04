vireo.directive("actionlog", function($filter, $timeout, NgTableParams) {
    return {
        templateUrl: "views/directives/actionLog.html",
        restrict: 'E',
        scope: {
            'submission': '=',
            'public': '=',
            'delay': '@'
        },
        link: function($scope) {

            var debounce = null;

            $scope.tableParams = new NgTableParams({
                sorting: {
                    id: "desc"
                }
            }, {
                total: $scope.submission.actionLogs.length,
                dataset: $scope.submission.actionLogs,
                getData: function(params) {
                    var data = [];
                    angular.extend(data, $scope.submission.actionLogs);

                    params.total(data.length);
                    data = $filter('orderBy')(data, params.orderBy());
                    return data.slice((params.page() - 1) * params.count(), params.page() * params.count());
                }
            });

            if (angular.isDefined($scope.submission.actionLogListenReloadDefer)) {
                $scope.submission.actionLogListenReloadDefer.promise.then(null, null, function(actionLogs) {
                    if (debounce === null) {
                        debounce = function() {
                            $scope.tableParams.reload();
                            debounce = null;
                        };

                        $timeout(debounce, $scope.delay);
                    }
                });
            }
        }
    };
});
