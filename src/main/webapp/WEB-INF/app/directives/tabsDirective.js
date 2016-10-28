vireo.directive("tabs", function() {
	return {
		template: '<div class="tabs"><span ng-transclude></span><hr></div>',
		restrict: 'E',
		replace: false,
		transclude: true,
		scope: false,
		controller: function($scope, $location, $routeParams) {
			this.activeTab = function(tab) {
				var active = false;
				if($routeParams.id !== undefined) {
					active = tab.includes($routeParams.tab + '/' + $routeParams.id);
				}
				else {
					active = tab.includes($routeParams.tab);
				}
				return active;
			};
			this.setActive = function(tab, html) {
				$location.url(tab);
			};
		}
	};
});

vireo.directive("tab", function($compile) {
	 return {
		template: '<span ng-class="{\'active\': activeTab(path)}" ng-click="setActive(path, html)" class="tab">{{label}}</span>',
		restrict: 'E',
		replace: false,
		transclude: false,
		require: '^tabs',
		scope: true,
		link: function ($scope, element, attr, parent) {
			angular.extend($scope, parent);
			angular.extend($scope, attr);
			
			var span = angular.element('<span ng-if="activeTab(path)">');			
			span.html("<ng-include src='view'></ng-include>");			
			var view = $compile(span)($scope);
			
			element.parent().parent().parent().after(view);			
	    }
	};
});
