vireo.directive("accordion", function () {
	return {
		template: '<div class="accordion" ng-transclude></div>',
		restrict: 'E',
		replace: true,
		transclude: true,
		scope: {
			'singleExpand': '='
		},
		controller: function($scope)  {

		},
		link: function($scope, element, attr) {
			
		}
	};
});

vireo.directive("pane", function($location, $timeout, $routeParams, AccordionService) {
	var count = 0;
	return {
        templateUrl:function(element, attr) {
			return attr.barView? attr.barView : 'views/directives/accordionPane.html';
        },
		restrict: 'E',
		replace: false,
		transclude: true,
		require: "^accordion",
		scope: true,
		link: function ($scope, element, attr, parent) {

			count++;

			angular.extend($scope, parent);
			
			var getPanes = function() {
				var panes = [];
				if($routeParams.pane !== undefined) {
					if(typeof $routeParams.pane == 'string') {
						panes.push($routeParams.pane);
					}
					else {
						panes = $routeParams.pane;
					}
				}
				return panes;
			};

			$scope.query = typeof attr.query != "undefined" ? attr.query : "pane" + count;
			
			$scope.expanded = false;

			$scope.toggleExpanded = function() {
				$scope.expanded ? $scope.close() : $scope.open();
			};

			$scope.open = function() {
				var panes = [];
				if(!$scope.$parent.$parent.singleExpand) {
					panes = getPanes();
				}
				else {
					AccordionService.closeAll();
				}
				if(panes.indexOf($scope.query) < 0) {
					panes.push($scope.query);
				}
				$location.search('pane', panes);
				if($scope.html === undefined) {
					$scope.loading = true;
					$scope.html = attr.html;
				}
				$scope.expanded = true;
			}

			$scope.close = function() {
				var panes = getPanes();
				if(panes.indexOf($scope.query) >= 0) {
					panes.splice(panes.indexOf($scope.query), 1);
				}
				$location.search('pane', panes);
				$scope.expanded = false;
			}

			$scope.loaded = function() {
				$timeout(function(){
					$scope.loading = false;	
				}, 500);
			}
			
			AccordionService.add($scope.query, {
				'open': $scope.open,
				'close': $scope.close
			});
			
			getPanes().indexOf($scope.query) >= 0 ? $scope.open() : $scope.close();
	    }
	};
});

vireo.service("AccordionService", function() {

	var AccordionService = this;

	var panes = {};

	AccordionService.add = function(id, pane) {
		panes[id] = pane;
	};

	AccordionService.remove = function(id) {
		panes[id] !== undefined ? deletepanes[id] : console.log('No pane with id:', id);
	};
	
	AccordionService.open = function(id) {
		panes[id] !== undefined ? panes[id].open() : console.log('No pane with id:', id);
	};
	
	AccordionService.close = function(id) {
		panes[id] !== undefined ? panes[id].close() : console.log('No pane with id:', id);
	};
	
	AccordionService.closeAll = function() {
		for(var i in panes) {
			panes[i].close();
		}
	};
	
	AccordionService.openAll = function() {
		for(var i in panes) {
			panes[i].open();
		}
	};

	return AccordionService;

});

