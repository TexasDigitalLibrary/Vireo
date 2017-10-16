vireo.directive("vireoAccordion", function () {
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

vireo.directive("vireoPane", function($location, $timeout, $routeParams, AccordionService) {
	var count = 0;
	return {
        templateUrl:function(element, attr) {
			return attr.barView? attr.barView : 'views/directives/accordionPane.html';
        },
		restrict: 'E',
		replace: false,
		transclude: true,
		require: "^vireoAccordion",
		scope: true,
		link: function ($scope, element, attr, parent) {

			count++;

            var panelBody = element.find(".panel-body:first");

            var panelRow = element.closest(".row");

            panelBody.scroll(function(event) {
                if(panelBody.scrollTop() <= panelRow.offset().top) {
                    panelBody.find(".trash-drop-zone").css('margin-top', panelBody.scrollTop());
                }
            });

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
				if($scope.expanded) {
					$scope.close();
				}
				else {
					$scope.open();
				}
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
			};

			$scope.close = function() {
				var panes = getPanes();
				if(panes.indexOf($scope.query) >= 0) {
					panes.splice(panes.indexOf($scope.query), 1);
				}
				$location.search('pane', panes);
				$scope.expanded = false;
			};

			$scope.loaded = function() {
				$timeout(function(){
					$scope.loading = false;
				}, 500);
			};

			AccordionService.add($scope.query, {
				'open': $scope.open,
				'close': $scope.close
			});

			if(getPanes().indexOf($scope.query) >= 0) {
				$scope.open();
			}
			else {
				$scope.close();
			}
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
		if(panes[id] !== undefined) {
			delete panes[id];
		}
		else {
			console.log('No pane with id:', id);
		}
	};

	AccordionService.open = function(id) {
		if(panes[id] !== undefined) {
			panes[id].open();
		}
		else {
			console.log('No pane with id:', id);
		}
	};

	AccordionService.close = function(id) {
		if(panes[id] !== undefined) {
			panes[id].close();
		}
		else {
			console.log('No pane with id:', id);
		}
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
