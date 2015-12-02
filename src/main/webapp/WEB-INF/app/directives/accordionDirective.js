vireo.directive("accordion", function() {
	return {
		template: '<div class="accordion" ng-transclude></div>',
		restrict: 'E',
		replace: true,
		transclude: true,
		scope: {},
		controller: function($scope)  {

			this.closeAll = function(id) {
				$scope.$broadcast("close", id);
			}

		}
	};
});

vireo.directive("pane", function($location, $timeout, $anchorScroll) {

	return {
		templateUrl: 'views/directives/accordionPane.html',
		restrict: 'E',
		replace: false,
		transclude: true,
		require: "^accordion",
		scope: true,
		link: function ($scope, element, attr, parent) {

			angular.extend($scope, parent);
			
			$scope.hash = attr.hash;

			$timeout(function() {
				$location.hash() == $scope.hash ? $scope.open() : $scope.close();
				$anchorScroll();
			});

			$scope.toggleExpanded = function() {
				$scope.closeAll($scope.$id);
				$scope.expanded ? $scope.close() : $scope.open();
			}

			$scope.open = function() {
				if(typeof $scope.html == "undefined") $scope.html = attr.html;
				$scope.expanded = true;
			}

			$scope.close = function() {
				$scope.expanded = false;
			}

			$scope.$on('close', function(event, id) {
				if(id != $scope.$id) $scope.expanded = $scope.close();
			});

	    }
	};
});