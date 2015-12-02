vireo.directive('tooltip', function ($timeout) {

	return {
		templateUrl: "views/directives/tooltip.html",
		replace: true,
		transclude: true,
		restrict: 'A',
		scope:true,
		link: function($scope, elem, attr) {

			$scope.tip = attr.tooltip;

			$scope.tipVisible = false;
			$scope.showTimer = {};
			$scope.tipStyles = {};

			$scope.showTip = function() {
				$scope.showTimer = $timeout(function() {
					$scope.tipVisible = true;
				}, 500);
			}

			$scope.hideTip = function() {
				$timeout.cancel($scope.showTimer);
				$scope.tipVisible = false;
			}

			$scope.toggleVisible = function() {
				$timeout.cancel($scope.showTimer);
				$scope.tipVisible = $scope.tipVisible ? false : true;
			}

			$scope.positionTip = function($event) {
				$scope.tipStyles["top"] = $event.offsetY + 20;
				$scope.tipStyles["left"] = $event.offsetX -	25;
			}

		}
	};
});