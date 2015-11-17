vireo.directive("tabs", function() {
	return {
		template: '<div class="tabs"><span ng-transclude></span></div>',
		restrict: 'E',
		replace: false,
		transclude: true,
		scope: {
			target: "@target"
		},
		controller: function($scope, $location, $routeParams, TabService) {
			this.activeTab = function(tab) {				
				return $routeParams.tab == tab;
			}

			this.setActive = function(tab, html) {
				$location.url("/settings/"+tab);
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
		scope: {},
		link: function ($scope, element, attr) { 	
			$scope.path = TabService.getTab(attr.for);
	    }
	};

});

vireo.service("TabService", function($q) {

	var TabService = this;
	var tabs = {};

	TabService.getTab = function(target){
		console.log(tabs);
		return tabs[target];
	};

	TabService.setTab = function(target, html){
		console.log(target);
		tabs[target] = html;
	};	

	return TabService;
	
});

