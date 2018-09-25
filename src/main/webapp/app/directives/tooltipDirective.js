vireo.directive('tooltip', function ($timeout) {
    return {
        templateUrl: "views/directives/tooltip.html",
        replace: true,
        restrict: 'A',
        scope: true,
        link: function($scope, elem, attr) {
            $scope.message = attr.tooltip;

            $scope.isOpen = false;

            var timer;

            var open = function() {
                $timeout.cancel(timer);
                $scope.isOpen = true;
            };

            var close = function() {
                $timeout.cancel(timer);
                timer = $timeout(function() {
                    $scope.isOpen = false;
                }, 250);
            };

            $scope.mouseEnter = function() {
                open();
                $timeout(function() {
                    angular.element('.popover').hover(function() {
                        open();
                    }, function() {
                        close();
                    });
                });
            };

            $scope.mouseLeave = function() {
                close();
            };

        }
    };
});
