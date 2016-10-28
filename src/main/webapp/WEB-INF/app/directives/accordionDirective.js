var getPanes = function() {
	var panes = angular.fromJson(localStorage.getItem('panes'));
	if(panes === null) {
		panes = [];
	}
	return panes;
};

var addPane = function(pane) {
	var panes = getPanes();
	if(panes.indexOf(pane) === -1) {
		panes.push(pane);
	}
	localStorage.setItem('panes', angular.toJson(panes));
};

var removePane = function(pane) {
	var panes = getPanes();
	panes.splice(panes.indexOf(pane), 1);
	localStorage.setItem('panes', angular.toJson(panes));
};

vireo.directive("accordion", function ($timeout, AccordionService) {
	return {
		template: '<div class="accordion" ng-transclude></div>',
		restrict: 'E',
		replace: true,
		transclude: true,
		scope: {
			'singleExpand': '='
		},
		controller: function($scope)  {
			$timeout(function() {
				var panes = getPanes();
				for(var i in panes) {
					AccordionService.open(panes[i]);
				}
			}, 500);
		},
		link: function($scope, element, attr) {
			
		}
	};
});

vireo.directive("pane", function($timeout, AccordionService) {
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

			$scope.query = typeof attr.query != "undefined" ? attr.query : "pane" + count;
			
			$scope.expanded = false;


			$scope.toggleExpanded = function() {
				$scope.expanded ? $scope.close() : $scope.open();
			};

			$scope.open = function() {
				if($scope.$parent.$parent.singleExpand) {
					AccordionService.closeAll();
					localStorage.setItem('panes', angular.toJson([]));
				}
				addPane($scope.query);
				if($scope.html === undefined) {
					$scope.loading = true;
					$scope.html = attr.html;
				}
				$scope.expanded = true;
			}

			$scope.close = function() {
				removePane($scope.query);
				$scope.expanded = false;
			}

			$scope.loaded = function() {
				$timeout(function(){
					$scope.loading = false;	
				}, 500);
			}
			
			AccordionService.add($scope.query, $scope.open, $scope.close);
	    }
	};
});

vireo.service("AccordionService", function() {

	var AccordionService = this;

	var panes = {};

	AccordionService.add = function(pane, open, close) {
		panes[pane] = {
			'open': open,
			'close': close
		}
	};

	AccordionService.remove = function(pane) {
		panes.splice(panes.indexOf(pane), 1);
	};
	
	AccordionService.open = function(pane) {
		for(var i in panes) {
			if(pane == i)  {
				panes[i].open();
			}
		}
	};
	
	AccordionService.close = function(pane) {
		for(var i in panes) {
			if(pane == i)  {
				panes[i].close();
			}
		}
	};

	AccordionService.closeAll = function(pane) {
		for(var i in panes) {
			panes[i].close();
		}
	};

	return AccordionService;

});
