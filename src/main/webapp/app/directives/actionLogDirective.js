vireo.directive("actionlog", function($filter, $timeout, NgTableParams) {
    return {
        templateUrl: "views/directives/actionLog.html",
        restrict: 'E',
        scope: {
            'submission': '=',
            'method': '&?',
            'delay': '@'
        },
        link: function($scope) {

            var debounce = null;

            $scope.tableParams = new NgTableParams({
                sorting: {
                    id: "desc"
                }
            }, {
                total: angular.isDefined($scope.submission.actionLogs) ? $scope.submission.actionLogs.length : 0,
                dataset: angular.isDefined($scope.submission.actionLogs) ? $scope.submission.actionLogs : [],
                getData: function(params) {
                    if (angular.isDefined($scope.method) && $scope.method !== null) {
                        var result = $scope.method()(params.orderBy(), params.page() - 1, params.count()).then(function (page) {
                            params.total(page.totalElements);

                            return page.content;
                        });

                        return result;
                    }

                    var data = [];

                    if (angular.isDefined($scope.submission.actionLogs)) {
                        angular.extend(data, $scope.submission.actionLogs);
                    }

                    params.total(data.length);
                    data = $filter('orderBy')(data, params.orderBy()).slice((params.page() - 1) * params.count(), params.page() * params.count());
                    return data;
                }
            });

            if (angular.isDefined($scope.submission.actionLogListenReloadDefer)) {
                $scope.submission.actionLogListenReloadDefer.promise.then(null, null, function (actionLogs) {
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
