vireo.directive('tooltip', function ($timeout, $compile) {

	return {
		template: '<span class="tooltip-handle" ng-mousemove="positionTip($event)" ng-mouseover="showTip()" ng-mouseout="hideTip()" ng-click="toggleVisible()" ng-transclude></span>',
		replace: true,
		transclude: true,
		restrict: 'A',
		scope:true,
		link: function($scope, elem, attr) {
			
			var tipTemplate = 	'<div class="tooltip-wrapper">'+
									'<div ng-style="tipStyles" class="tip" ng-class="{\'tip-visible\': tipVisible, \'hidden\': hidden}">'+
										'<div class="tip-point"></div>' +
										'<div class="tip-message">{{::tip}}</div>' +
									'</div>' +
								'</div>';
			
			angular.element("body").append($compile(tipTemplate)($scope));

			$scope.tip = attr.tooltip;

			$scope.tipVisible = false;
			$scope.hidden = true;
			$scope.showTimer = {};
			$scope.tipStyles = {};

			$scope.showTip = function() {
				$scope.hidden = false;
				$scope.showTimer = $timeout(function() {
					$scope.tipVisible = true;
				}, 500);
			}

			$scope.hideTip = function() {
				$timeout.cancel($scope.showTimer);
				$scope.tipVisible = false;

				$timeout(function() {
					$scope.hidden = true;
				}, 500);

			}

			$scope.toggleVisible = function() {
				$timeout.cancel($scope.showTimer);
				$scope.tipVisible = $scope.tipVisible ? false : true;

				if(!$scope.tipVisible) {
					$timeout(function() {
						$scope.hidden = $scope.hidden ? false : true;
					}, 500);
				} 

			}

			$scope.positionTip = function($event) {
				$scope.tipStyles["top"] = $event.clientY + 20;
				$scope.tipStyles["left"] = $event.clientX -	25;
			}
		}
	};
});