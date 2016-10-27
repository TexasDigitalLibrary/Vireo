vireo.directive("tabs", function() {
	return {
		template: '<div class="tabs"><span ng-transclude></span><hr></div>',
		restrict: 'E',
		replace: false,
		transclude: true,
		scope: {
			target: "@target",
			param: "="
		},
		controller: function($scope, $location, $routeParams, TabService) {
			
			this.activeTab = function(tab) {
				return tab.includes($routeParams.tab);
			}

			this.setActive = function(tab, html) {
				var path = tab + ($scope.param !== undefined ? '/' + $scope.param : '');				
				$location.url(path);
				TabService.setTab($scope.target, html);
			}

			this.target = $scope.target;
		},
		link: function ($scope, element, attr) {	    	
			$scope.target = attr.target;
	    }
	};
});

vireo.directive("tab", function(TabService) {
	 return {
		template: '<span ng-class="{\'active\': activeTab(tab)}" ng-click="setActive(tab, html)" class="tab"><span ng-transclude></span></span>',
		restrict: 'E',
		replace: false,
		transclude: true,
		require: '^tabs',
		scope: true,
		link: function ($scope, element, attr, parent) {
			
			angular.extend($scope, parent);
			angular.extend($scope, attr);

			$scope.tab = $scope.path;

			if($scope.activeTab($scope.tab)) {
				TabService.setTab($scope.target, $scope.html);
			}

	    }
	};
});

vireo.directive("tabview", function(TabService) {

	 return {
		template: '<span ng-include="path"></span>',
		restrict: 'E',
		replace: false,
		scope: false,
		link: function ($scope, element, attr) { 	
			$scope.path = TabService.getTab(attr.for);
	    }
	};

});

vireo.service("TabService", function($q) {

	var TabService = this;
	var tabs = {};

	TabService.getTab = function(target){
		return tabs[target];
	};

	TabService.setTab = function(target, html){
		tabs[target] = html;
	};	

	return TabService;
	
});

