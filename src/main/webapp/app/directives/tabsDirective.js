vireo.directive("vireoTabs", function() {
	return {
		template: '<div id="tabs-directive" class="tabs"><span ng-transclude></span><hr></div>',
		restrict: 'E',
		replace: false,
		transclude: true,
		scope: false,
		controller: function($scope, $routeParams, VireoTabService) {
			var initialized = false;
			$scope.activeTab = function(path) {
				if(!initialized) {
					var active = false;
					if($routeParams.id !== undefined) {
						active = path.indexOf('/' + $routeParams.id + '/' + $routeParams.tab) >= 0;
					}
					else {
						active = path.indexOf('/' + $routeParams.tab) >= 0;
					}
					if(active) {
						initialized = true;
						$scope.setActive(path);
					}
				}
				return VireoTabService.isActive(path);
			};
			$scope.setActive = function(path) {
				VireoTabService.activate(path);
			};
		}
	};
});

vireo.directive("vireoTab", function($compile, $location, VireoTabService, WsApi) {
	 return {
		template: '<span ng-class="{\'active\': activeTab(path)}" ng-click="setActive(path)" class="tab">{{label}}</span>',
		restrict: 'E',
		replace: false,
		transclude: false,
		require: '^vireoTabs',
		scope: true,
		link: function ($scope, attr, parent) {
			angular.extend($scope, parent);
			angular.extend($scope, attr);

			$scope.reload = angular.isDefined($scope.reload) ? ($scope.reload === 'false') ? false : true : true;

			var span = angular.element('<span id="'+($scope.path.replace(/\//g, "-"))+'" ng-if="activeTab(path)">');
			span.html("<ng-include src='view'></ng-include>");

			if(angular.element("#"+$scope.path.replace(/\//g, "-")).length!==0) {
				angular.element("#"+$scope.path.replace(/\//g, "-")).remove();
			}
			angular.element('#tabs-directive').after($compile(span)($scope));

			VireoTabService.register($scope.path, function() {
				if($scope.reload === false) {
					WsApi.registerPersistentRouteBasedChannel($scope.path);
				}
				$location.path($scope.path, $scope.reload);
			});

	    }
	};
});

vireo.service("VireoTabService", function() {
	var tabs = {};
	var active;
	return {
		register: function(path, loader) {
			tabs[path] = {
				load: loader
			};
		},
		activate: function(path) {
			tabs[path].load();
			active = path;
		},
		isActive: function(path) {
			return active === path;
		}
	};
});