vireo.directive("vireoTabs", function() {
	return {
		template: '<div id="tabs-directive" class="tabs"><span ng-transclude></span><hr></div>',
		restrict: 'E',
		replace: false,
		transclude: true,
		scope: false,
		controller: function($scope, $location, $routeParams) {
			this.activeTab = function(path) {
				var active = false;
				if($routeParams.id !== undefined) {
					active = path.includes('/' + $routeParams.id + '/' + $routeParams.tab);
				}
				else {
					active = path.includes('/' + $routeParams.tab);
				}
				return active;
			};
			this.setActive = function(path) {
				$location.path(path);
			};
		}
	};
});

vireo.directive("vireoTab", function($compile) {
	 return {
		template: '<span ng-class="{\'active\': activeTab(path)}" ng-click="setActive(path)" class="tab">{{label}}</span>',
		restrict: 'E',
		replace: false,
		transclude: false,
		require: '^vireoTabs',
		scope: true,
		link: function ($scope, element, attr, parent) {
			angular.extend($scope, parent);
			angular.extend($scope, attr);

			var span = angular.element('<span id="'+($scope.path.replace(/\//g, "-"))+'" ng-if="activeTab(path)">');
			span.html("<ng-include src='view'></ng-include>");

			if(angular.element("#"+$scope.path.replace(/\//g, "-")).length!==0) {
				angular.element("#"+$scope.path.replace(/\//g, "-")).remove();
			}
			angular.element('#tabs-directive').after($compile(span)($scope));

	    }
	};
});
