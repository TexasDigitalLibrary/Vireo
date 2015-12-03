vireo.directive("accordion", function() {
	return {
		template: '<div class="accordion" ng-transclude></div>',
		restrict: 'E',
		replace: true,
		transclude: true,
		scope: {
			singleExpand: "@singleExpand"
		},
		controller: function($scope)  {

			this.closeAll = function(id) {
				if($scope.singleExpand == "true") $scope.$broadcast("close", id);
			}

		},
		link: function($scope, element, attr) {
			
			$scope.singleExpand = typeof attr.singleExpand != "undefined" ? attr.singleExpand.toLowerCase() == "true" : false;	
			
				
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
				$location.hash() == $scope.hash ? $scope.open(true) : $scope.close();
				$anchorScroll();
			});

			$scope.toggleExpanded = function() {
				$scope.closeAll($scope.$id);
				$scope.expanded ? $scope.close() : $scope.open();
			}

			$scope.open = function(pageLoad) {
				if(typeof $scope.html == "undefined") {
					$scope.loading = true;
					$scope.html = attr.html;
				}
				$scope.expanded = true;
				if(!pageLoad) {
					$location.hash($scope.hash, false);
					$anchorScroll();	
				}
			}

			$scope.close = function() {
				$scope.expanded = false;
			}

			$scope.loaded = function() {
				$timeout(function(){
					$scope.loading = false;	
				}, 500);
			}

			$scope.$on('close', function(event, id) {
				if(id != $scope.$id) $scope.expanded = $scope.close();
			});

	    }
	};
});