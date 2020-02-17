vireo.directive('weaverTable', function () {
    return {
        templateUrl: 'views/directives/tableControls.html',
        restrict: 'E',
        replace: false,
        transclude: {
            table: 'weaverTableElement',
            controls: 'weaverTableControls',
            modals: 'weaverTableModals'
        },
        scope: {
            weaverTable: "="
        },
        controller: ['$scope', function ($scope) {

            $scope.filters = $scope.weaverTable.columns.filter(function (column) {
                return column.filterable;
            });

            $scope.filter = $scope.filters[0];

            $scope.activeFilters = $scope.weaverTable.pageSettings.filters;

            $scope.selectFilter = function (filter) {
                $scope.filter = filter;
            };

            $scope.removeFilter = function (prop, v) {
                $scope.activeFilters[prop].splice($scope.activeFilters[prop].indexOf(v), 1);
                if ($scope.activeFilters[prop].length === 0) {
                    delete $scope.activeFilters[prop];
                }
                $scope.weaverTable.tableParams.reload();
            };

            $scope.applyFilter = function (filter) {
                if (filter.isConstant) {
                    filter.value = filter.value.replace(' ', '_');
                }
                if ($scope.activeFilters[filter.property]) {
                    $scope.activeFilters[filter.property].push(filter.value);
                } else {
                    $scope.activeFilters[filter.property] = [filter.value];
                }
                $scope.weaverTable.tableParams.reload();
                delete $scope.filter.value;
            };

            $scope.lookupGloss = function (prop) {
                for (var i in $scope.filters) {
                    var filter = angular.copy($scope.filters[i]);
                    if (filter.property === prop) {
                        return filter.gloss;
                    }
                }
            };

            var activeSort = $scope.weaverTable.pageSettings.sort = $scope.weaverTable.activeSort;

            $scope.weaverTable.unsorted = function (prop) {
                for (var i in activeSort) {
                    var sort = activeSort[i];
                    if (sort.property === prop) {
                        return false;
                    }
                }
                return true;
            };

            $scope.weaverTable.asc = function (prop) {
                for (var i in activeSort) {
                    var sort = activeSort[i];
                    if (sort.property === prop && sort.direction === 'ASC') {
                        return true;
                    }
                }
                return false;
            };

            $scope.weaverTable.desc = function (prop) {
                for (var i in activeSort) {
                    var sort = activeSort[i];
                    if (sort.property === prop && sort.direction === 'DESC') {
                        return true;
                    }
                }
                return false;
            };

            $scope.weaverTable.toggleSort = function (prop) {
                var asc = true;
                for (var i in activeSort) {
                    var sort = activeSort[i];
                    if (sort.property === prop) {
                        if (sort.direction === 'ASC') {
                            sort.direction = 'DESC';
                        } else {
                            activeSort.splice(i, 1);
                        }
                        asc = false;
                        break;
                    }
                }
                if (asc) {
                    activeSort.push({
                        property: prop,
                        direction: 'ASC'
                    });
                }
                $scope.weaverTable.tableParams.reload();
            };

        }]
    };
});
